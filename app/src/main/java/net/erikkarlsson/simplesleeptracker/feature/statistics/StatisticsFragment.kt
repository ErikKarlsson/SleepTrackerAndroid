package net.erikkarlsson.simplesleeptracker.feature.statistics

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProvider
import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.content.res.ResourcesCompat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.view.isVisible
import com.jakewharton.rxbinding2.view.clicks
import com.jakewharton.rxbinding2.widget.itemSelections
import dagger.android.support.AndroidSupportInjection
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import kotlinx.android.synthetic.main.fragment_statistics.*
import net.erikkarlsson.simplesleeptracker.R
import net.erikkarlsson.simplesleeptracker.di.ViewModelFactory
import net.erikkarlsson.simplesleeptracker.domain.entity.Sleep
import net.erikkarlsson.simplesleeptracker.domain.entity.StatisticComparison
import net.erikkarlsson.simplesleeptracker.elm.ElmViewModel
import net.erikkarlsson.simplesleeptracker.feature.statistics.chart.AverageTimeChartRenderer
import net.erikkarlsson.simplesleeptracker.feature.statistics.chart.BasicChartRenderer
import net.erikkarlsson.simplesleeptracker.feature.statistics.chart.SleepDurationChartRenderer
import net.erikkarlsson.simplesleeptracker.util.formatDateDisplayName
import net.erikkarlsson.simplesleeptracker.util.formatHHMM
import net.erikkarlsson.simplesleeptracker.util.formatHoursMinutes
import net.erikkarlsson.simplesleeptracker.util.formatHoursMinutesWithPrefix
import timber.log.Timber
import javax.inject.Inject

data class ChartExtra(val displayValue: Boolean)

class StatisticsFragment : Fragment() {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    @Inject
    lateinit var basicChartRenderer: BasicChartRenderer

    @Inject
    lateinit var sleepDurationChartRenderer: SleepDurationChartRenderer

    @Inject
    lateinit var averageTimeChartRenderer: AverageTimeChartRenderer

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

        spinner.setSelection(1) // TODO (erikkarlsson): Hardcoded week selection.

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

            sleepDurationChartRenderer.render(sleepDurationChart, state.statistics)

            with(state.statistics.first) {
                if (!isEmpty) {
                    trackedNightsText.text = sleepCount.toString()
                    avgDurationText.text = String.format("%s", avgSleepHours.formatHoursMinutes)

                    renderAvgTimeDiff(avgDurationDiffText, state.statistics.avgSleepDiffHours)
                    renderLongestNight(longestSleep)
                    renderShortestNight(shortestSleep, longestSleep)
                    renderAvgTimeDiff(avgBedDiffText, state.statistics.avgBedTimeDiffHours)
                    renderAvgTimeDiff(avgWakeUpDiffText, state.statistics.avgWakeUpTimeDiffHours)
                    renderAverageBedTime(state.statistics)
                    renderAverageWakeUpTime(state.statistics)
                }
            }
        }
    }

    private fun renderAvgTimeDiff(avgTimeText: TextView,
                                  avgTimeDiffHours: Float) {
        val avgDiffTextColor = if (avgTimeDiffHours > 0) {
            R.color.green
        } else {
            R.color.red
        }

        avgTimeText.setTextColor(ResourcesCompat.getColor(resources, avgDiffTextColor, null))

        if (avgTimeDiffHours == 0f) {
            avgTimeText.isVisible = false
        } else {
            avgTimeText.isVisible = true
            avgTimeText.text = String.format("%s",
                    avgTimeDiffHours.formatHoursMinutesWithPrefix)
        }
    }

    private fun renderAverageBedTime(statistics: StatisticComparison) {
        val current = statistics.first
        val averageBedTime = current.averageBedTime
        averageBedText.text = averageBedTime.formatHHMM
        averageTimeChartRenderer.render(averageBedTimeChart, current.averageBedTime,
                statistics.first.averageBedTimeDayOfWeek, statistics.first)
    }

    private fun renderAverageWakeUpTime(statistics: StatisticComparison) {
        val current = statistics.first
        val averageWakeUpTime = current.averageWakeUpTime
        averageWakeUpText.text = averageWakeUpTime.formatHHMM
        averageTimeChartRenderer.render(averageWakeUpTimeChart, current.averageWakeUpTime,
                statistics.first.averageWakeUpTimeDayOfWeek, statistics.first)
    }

    private fun renderLongestNight(longestSleep: Sleep) {
        longestNightDurationText.text = longestSleep.hours.formatHoursMinutes
        longestNightDateText.text = longestSleep.toDate?.formatDateDisplayName

        val value = if (longestSleep.hours == 0f) {
            0f
        } else {
            1f
        }

        basicChartRenderer.render(longestNightChart, value, 1f)
    }

    private fun renderShortestNight(shortestSleep: Sleep, longestSleep: Sleep) {
        shortestNightDurationText.text = shortestSleep.hours.formatHoursMinutes
        shortestNightDateText.text = shortestSleep.toDate?.formatDateDisplayName

        val value = shortestSleep.hours
        val maxValue = longestSleep.hours

        basicChartRenderer.render(shortestNightChart, value, maxValue)
    }

    private fun onFilterSelected(index: Int) {
        val statisticsFilter = StatisticsFilter.values()[index]
        viewModel.dispatch(StatisticsFilterSelected(statisticsFilter))
    }
}

class StatisticsViewModel @Inject constructor(statisticsComponent: StatisticsComponent) :
        ElmViewModel<StatisticsState, StatisticsMsg, StatisticsCmd>(statisticsComponent)
