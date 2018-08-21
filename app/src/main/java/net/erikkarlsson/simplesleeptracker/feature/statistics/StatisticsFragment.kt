package net.erikkarlsson.simplesleeptracker.feature.statistics

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProvider
import android.content.Context
import android.content.res.Resources
import android.graphics.Color
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.content.res.ResourcesCompat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.components.*
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet
import com.jakewharton.rxbinding2.view.clicks
import com.jakewharton.rxbinding2.widget.itemSelections
import dagger.android.support.AndroidSupportInjection
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import kotlinx.android.synthetic.main.fragment_statistics.*
import net.erikkarlsson.simplesleeptracker.R
import net.erikkarlsson.simplesleeptracker.di.ViewModelFactory
import net.erikkarlsson.simplesleeptracker.elm.ElmViewModel
import timber.log.Timber
import javax.inject.Inject
import com.github.mikephil.charting.formatter.IAxisValueFormatter
import com.github.mikephil.charting.formatter.IValueFormatter
import kotlinx.android.synthetic.main.notification_template_lines_media.*
import net.erikkarlsson.simplesleeptracker.domain.entity.StatisticComparison
import net.erikkarlsson.simplesleeptracker.util.*
import org.threeten.bp.DayOfWeek
import org.threeten.bp.format.TextStyle
import java.util.*

data class ChartExtra(val displayValue: Boolean)

private const val BAR_WIDTH = 0.5f
private const val ANIMATION_DURATION = 1300
private const val AXIS_TEXT_SIZE = 12f
private const val VALUE_TEXT_SIZE = 10f

class StatisticsFragment : Fragment() {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    @Inject
    lateinit var ctx: Context

    private val viewModel: StatisticsViewModel by lazy {
        ViewModelProvider(this, viewModelFactory).get(StatisticsViewModel::class.java)
    }

    private val disposables = CompositeDisposable()

    override fun onAttach(context: Context?) {
        AndroidSupportInjection.inject(this)
        super.onAttach(context)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_statistics, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val spinnerAdapter = ArrayAdapter.createFromResource(ctx, R.array.statistic_filter_array, android.R.layout.simple_spinner_item)
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = spinnerAdapter

        viewModel.state().observe(this, Observer { render(it) })
    }

    override fun onStart() {
        super.onStart()

        spinner.itemSelections().subscribe { onFilterSelected(it) }.addTo(disposables)

        Observable.merge(toggleSleepButton.clicks(), owlImage.clicks())
                .subscribe({ viewModel.dispatch(ToggleSleepClicked) },
                        { Timber.e(it, "Error merging clicks") })
                .addTo(disposables)
    }

    override fun onStop() {
        super.onStop()
        disposables.clear()
    }

    private fun render(state: StatisticsState?) {
        state?.let {
            val imageRes = if (state.isSleeping) R.drawable.owl_asleep else R.drawable.own_awake
            owlImage.setImageResource(imageRes)

            renderSleepDuration(state.statistics)

            with(state.statistics.first) {
                statisticsText.text = if (this.isEmpty) {
                    getString(R.string.no_sleep_tracked_yet)
                } else {

                    trackedNightsText.text = sleepCount.toString()

                    avgDurationText.text = String.format("%s",
                            avgSleepHours.formatHoursMinutes)

                    renderAvgDurationDiff(state.statistics.avgSleepDiffHours)

                    val longestNight = String.format("%s: %s, %s\n",
                            getString(R.string.longest_night),
                            longestSleep.hours.formatHoursMinutes,
                            longestSleep.toDate?.formatDateDisplayName)

                    val shortestNight = String.format("%s: %s, %s\n",
                            getString(R.string.shortest_night),
                            shortestSleep.hours.formatHoursMinutes,
                            shortestSleep.toDate?.formatDateDisplayName)

                    val averageBedTime = String.format("%s: %s %s\n%s\n",
                            getString(R.string.average_bed_time),
                            averageBedTime.formatHHMM,
                            with(state.statistics.avgBedTimeDiffHours) {
                                if (this == 0f) "" else String.format("(%s)", formatHoursMinutesWithPrefix)
                            },
                            averageBedTimeDayOfWeek.formatDisplayNameTime)

                    val averageWakeUpTime = String.format("%s: %s %s\n%s\n",
                            getString(R.string.average_wake_up_time),
                            averageWakeUpTime.formatHHMM,
                            with(state.statistics.avgWakeUpTimeDiffHours) {
                                if (this == 0f) "" else String.format("(%s)", formatHoursMinutesWithPrefix)
                            },
                            averageWakeUpTimeDayOfWeek.formatDisplayNameTime)


                    longestNight + shortestNight + averageBedTime + averageWakeUpTime
                }
            }
        }
    }

    private fun renderAvgDurationDiff(avgSleepDiffHours: Float) {
        val avgDurationDiffTextColor = if (avgSleepDiffHours > 0) {
            R.color.green
        } else {
            R.color.red
        }

        avgDurationDiffText.setTextColor(ResourcesCompat.getColor(resources, avgDurationDiffTextColor, null))

        avgDurationDiffText.text = with(avgSleepDiffHours) {
            if (this == 0f) {
                ""
            } else {
                String.format("%s", formatHoursMinutesWithPrefix)
            }
        }
    }

    private fun renderSleepDuration(statisticsComparison: StatisticComparison) {
        val current = statisticsComparison.first
        val previous = statisticsComparison.second

        val pinkValues = ArrayList<BarEntry>()
        val greenValues = ArrayList<BarEntry>()
        val lightPinkValues = ArrayList<BarEntry>()

        // Monday gets highest x value and Sunday lowest to show them in ascending order in chart.
        for (day in 6 downTo 0) {
            val x = 6 - day.toFloat()

            val dayOfWeekHours = current.averageSleepDurationDayOfWeekFor(day)
            val previousDayOfWeekHours = previous.averageSleepDurationDayOfWeekFor(day)

            when {
                dayOfWeekHours == null -> {
                    pinkValues.add(BarEntry(x, 0f, ChartExtra(false)))
                    greenValues.add(BarEntry(x, 0f, ChartExtra(false)))
                    lightPinkValues.add(BarEntry(x, 0f, ChartExtra(false)))
                }
                previousDayOfWeekHours == null -> {
                    pinkValues.add(BarEntry(x, dayOfWeekHours.hours, ChartExtra(true)))
                    greenValues.add(BarEntry(x, 0f, ChartExtra(false)))
                    lightPinkValues.add(BarEntry(x, 0f, ChartExtra(false)))
                }
                else -> {
                    val previousHours = previousDayOfWeekHours.hours
                    val currentHours = dayOfWeekHours.hours
                    val diffHours = Math.abs(currentHours - previousHours)

                    var lightPinkVal = 0f

                    if (currentHours >= previousHours) {
                        val pinkVal = currentHours - diffHours
                        pinkValues.add(BarEntry(x, pinkVal, ChartExtra(false)))
                        greenValues.add(BarEntry(x, currentHours, ChartExtra(true)))
                    } else {
                        lightPinkVal = previousHours

                        pinkValues.add(BarEntry(x, currentHours, ChartExtra(true)))
                        greenValues.add(BarEntry(x, 0f, ChartExtra(false)))
                    }

                    lightPinkValues.add(BarEntry(x, lightPinkVal, ChartExtra(false)))
                }
            }
        }

        val set1 = BarDataSet(pinkValues, "Sleep duration")
        set1.color = ResourcesCompat.getColor(ctx.resources, R.color.colorAccent, null)
        set1.valueTextSize = VALUE_TEXT_SIZE

        val set2 = BarDataSet(greenValues, "Sleep duration add")
        set2.color = ResourcesCompat.getColor(ctx.resources, R.color.green, null)
        set2.valueTextSize = VALUE_TEXT_SIZE

        val set3 = BarDataSet(lightPinkValues, "Sleep duration sub")
        set3.color = ResourcesCompat.getColor(ctx.resources, R.color.pink_light, null)

        val dataSets = ArrayList<IBarDataSet>()
        dataSets.add(set3)
        dataSets.add(set2)
        dataSets.add(set1)

        val data = BarData(dataSets)
        data.barWidth = BAR_WIDTH
        data.setValueFormatter(IValueFormatter { value, entry, _, _ ->
            if (entry == null) {
                return@IValueFormatter ""
            }

            val chartExtra = entry.data as ChartExtra

            if (chartExtra.displayValue) {
                value.formatHoursMinutes3
            } else {
                ""
            }
        })

        val longestSleepDurationDayOfWeekInHours = statisticsComparison.first.longestSleepDurationDayOfWeekInHours
        val leftMaximum = Math.round(longestSleepDurationDayOfWeekInHours.toDouble()).toFloat() + 1f

        val leftAxis = sleepDurationChart.axisLeft
        leftAxis.isEnabled = true
        leftAxis.axisMinimum = 0f
        leftAxis.axisMaximum = leftMaximum
        leftAxis.textSize = AXIS_TEXT_SIZE
        leftAxis.valueFormatter = IAxisValueFormatter { value, _ -> String.format("%dh", value.toInt()) }

        val rightAxis = sleepDurationChart.axisRight
        rightAxis.isEnabled = false

        val xAxis = sleepDurationChart.xAxis
        xAxis.isEnabled = true
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.granularity = 1f
        xAxis.textSize = AXIS_TEXT_SIZE
        xAxis.setDrawGridLines(false)
        xAxis.valueFormatter = IAxisValueFormatter { value, _ -> DayOfWeek.of(7 - value.toInt()).getDisplayName(TextStyle.SHORT, Locale.getDefault()) }

        val legend = sleepDurationChart.legend
        legend.isEnabled = false

        sleepDurationChart.data = data
        sleepDurationChart.setScaleEnabled(false)
        sleepDurationChart.setTouchEnabled(false)
        sleepDurationChart.isDragEnabled = false
        sleepDurationChart.description.text = ""
        sleepDurationChart.animateY(ANIMATION_DURATION, Easing.EasingOption.EaseOutQuad)
        sleepDurationChart.setBackgroundColor(ResourcesCompat.getColor(ctx.resources,
                android.R.color.transparent,
                null))
        sleepDurationChart.invalidate()
    }

    private fun onFilterSelected(index: Int) {
        val statisticsFilter = StatisticsFilter.values()[index]
        viewModel.dispatch(StatisticsFilterSelected(statisticsFilter))
    }
}

class StatisticsViewModel @Inject constructor(statisticsComponent: StatisticsComponent) :
        ElmViewModel<StatisticsState, StatisticsMsg, StatisticsCmd>(statisticsComponent)
