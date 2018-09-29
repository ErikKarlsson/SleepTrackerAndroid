package net.erikkarlsson.simplesleeptracker.feature.statistics.chart

import android.content.Context
import android.graphics.Color
import android.support.v4.content.res.ResourcesCompat
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.LimitLine
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IAxisValueFormatter
import com.google.common.collect.ImmutableList
import net.erikkarlsson.simplesleeptracker.R
import net.erikkarlsson.simplesleeptracker.base.SECONDS_IN_AN_HOUR
import net.erikkarlsson.simplesleeptracker.domain.entity.DayOfWeekLocalTime
import net.erikkarlsson.simplesleeptracker.domain.entity.Statistics
import net.erikkarlsson.simplesleeptracker.feature.statistics.AXIS_TEXT_SIZE
import net.erikkarlsson.simplesleeptracker.feature.statistics.VALUE_TEXT_SIZE
import net.erikkarlsson.simplesleeptracker.util.formatHHMM
import net.erikkarlsson.simplesleeptracker.util.midnightOffsetInSeconds
import org.threeten.bp.DayOfWeek
import org.threeten.bp.LocalTime
import org.threeten.bp.format.TextStyle
import timber.log.Timber
import java.util.*
import javax.inject.Inject

class AverageTimeChartRenderer @Inject constructor(private val ctx: Context) {

    fun render(avergeTimeChart: BarChart,
               averageTime: LocalTime,
               timeDayOfWeek: ImmutableList<DayOfWeekLocalTime>,
               statistics: Statistics) {

        val rightAxis = avergeTimeChart.getAxisRight()
        rightAxis.setEnabled(false)

        val leftAxis = avergeTimeChart.axisLeft
        leftAxis.isEnabled = true
        leftAxis.setDrawGridLines(false)
        leftAxis.setDrawAxisLine(false)
        leftAxis.setDrawLabels(false)
        leftAxis.setDrawZeroLine(true)
        leftAxis.textSize = AXIS_TEXT_SIZE

        val limitLine = LimitLine(0f)
        limitLine.lineColor = Color.GRAY
        limitLine.lineWidth = 0.4f
        leftAxis.addLimitLine(limitLine)

        val xAxis = avergeTimeChart.getXAxis()
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM)
        xAxis.setDrawGridLines(false)
        xAxis.setDrawAxisLine(false)
        xAxis.setTextSize(AXIS_TEXT_SIZE)
        xAxis.setGranularity(1f)
        xAxis.valueFormatter = IAxisValueFormatter { value, _ -> DayOfWeek.of(7 - value.toInt()).getDisplayName(TextStyle.SHORT, Locale.getDefault()) }

        val legend = avergeTimeChart.getLegend()
        legend.isEnabled = false

        val yValues = ArrayList<BarEntry>()
        val colors = ArrayList<Int>()

        val averageMidnightOffsetInSeconds = averageTime.midnightOffsetInSeconds

        var maxDiff = -99999999
        var minDiff = 99999999

        val green = Color.rgb(110, 190, 102)
        val red = Color.rgb(211, 74, 88)

        for (day in 7 downTo 1) {
            val x = 7 - day.toFloat()

            val dayOfWeekLocalTime = statistics.timeDayOfWeekFor(timeDayOfWeek, day)

            if (dayOfWeekLocalTime == null) {
                yValues.add(BarEntry(x, floatArrayOf(0.000001f, 0.000001f)))
            } else {
                val localTime = dayOfWeekLocalTime.localTime
                val midnightOffsetInSeconds = localTime.midnightOffsetInSeconds
                val midnightOffsetInSecondsDiff = midnightOffsetInSeconds - averageMidnightOffsetInSeconds

                if (midnightOffsetInSecondsDiff > maxDiff) {
                    maxDiff = midnightOffsetInSecondsDiff
                } else if (midnightOffsetInSecondsDiff < minDiff) {
                    minDiff = midnightOffsetInSecondsDiff
                }

                val isLaterThanAverage = midnightOffsetInSeconds >= averageMidnightOffsetInSeconds
                if (isLaterThanAverage) {
                    val y = floatArrayOf(0.000001f, midnightOffsetInSecondsDiff.toFloat())
                    yValues.add(BarEntry(x, y, localTime))
                    colors.add(green)
                } else {
                    val y = floatArrayOf(midnightOffsetInSecondsDiff.toFloat(), 0.000001f)
                    yValues.add(BarEntry(x, y, localTime))
                    colors.add(red)
                }

            }
        }

        // Max diff not found.
        if (maxDiff < 0) {
            maxDiff = 0
        }

        // Min diff not found.
        if (minDiff > 0) {
            minDiff = 0
        }

        var maxValue = Math.max(Math.abs(maxDiff), Math.abs(minDiff))

        // Max value of 0 means all bars are in the middle of the chart.
        // To fix bug with values not rendering we set max value to 1.
        if (maxValue == 0) {
            maxValue = 1
        }

        val scale = if (maxValue < SECONDS_IN_AN_HOUR / 2) {
            2.5f
        } else {
            1.4f
        }

        val yMin = maxValue * scale * -1
        val yMax = maxValue * scale

        leftAxis.setAxisMinimum(yMin.toFloat())
        leftAxis.setAxisMaximum(yMax.toFloat())

        val set = BarDataSet(yValues, "Average Bed Time")
        set.valueTextSize = VALUE_TEXT_SIZE
        set.setColors(ResourcesCompat.getColor(ctx.resources, R.color.pink_light, null),
                ResourcesCompat.getColor(ctx.resources, R.color.colorAccent, null))

        val data = BarData(set)
        data.barWidth = 0.5f
        data.setValueFormatter { value, entry, _, _ ->
            Timber.d("value %f", value)
            if (value == 0.000001f) {
                ""
            } else {
                val localTime = entry.data as LocalTime
                localTime.formatHHMM
            }
        }

        avergeTimeChart.setData(data)
        avergeTimeChart.setDrawBarShadow(false)
        avergeTimeChart.setDrawValueAboveBar(true)
        avergeTimeChart.setHighlightFullBarEnabled(false)
        avergeTimeChart.setDrawGridBackground(false)
        avergeTimeChart.getDescription().setEnabled(false)
        avergeTimeChart.setScaleEnabled(false)
        avergeTimeChart.setPinchZoom(false)
        avergeTimeChart.setTouchEnabled(false)
        avergeTimeChart.isDragEnabled = false
        avergeTimeChart.description.text = ""
        avergeTimeChart.invalidate()
    }
}