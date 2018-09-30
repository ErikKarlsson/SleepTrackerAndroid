package net.erikkarlsson.simplesleeptracker.feature.statistics.chart

import android.content.Context
import android.support.v4.content.res.ResourcesCompat
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IAxisValueFormatter
import com.github.mikephil.charting.formatter.IValueFormatter
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet
import net.erikkarlsson.simplesleeptracker.R
import net.erikkarlsson.simplesleeptracker.domain.entity.StatisticComparison
import net.erikkarlsson.simplesleeptracker.feature.statistics.ANIMATION_DURATION
import net.erikkarlsson.simplesleeptracker.feature.statistics.AXIS_TEXT_SIZE
import net.erikkarlsson.simplesleeptracker.feature.statistics.BAR_WIDTH
import net.erikkarlsson.simplesleeptracker.feature.statistics.VALUE_TEXT_SIZE
import net.erikkarlsson.simplesleeptracker.feature.statistics.item.ChartExtra
import net.erikkarlsson.simplesleeptracker.util.formatHoursMinutes3
import org.threeten.bp.DayOfWeek
import org.threeten.bp.format.TextStyle
import java.util.*
import javax.inject.Inject

private const val MAX_VALUE_WHEN_EMPTY = 8

class SleepDurationChartRenderer @Inject constructor(private val ctx: Context) {

    fun render(sleepDurationChart: BarChart,
               statisticsComparison: StatisticComparison) {
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
        leftAxis.granularity = 1f
        leftAxis.textSize = AXIS_TEXT_SIZE
        leftAxis.valueFormatter = IAxisValueFormatter { value, _ ->
            // When empty display 8h instead of 1h as max value.
            val displayValue = if (value != 0f && statisticsComparison == StatisticComparison.empty()) {
                MAX_VALUE_WHEN_EMPTY
            } else {
                value.toInt()
            }

            String.format("%dh", displayValue)
        }

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

}