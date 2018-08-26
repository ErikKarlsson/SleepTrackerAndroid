package net.erikkarlsson.simplesleeptracker.feature.statistics.chart

import android.content.Context
import android.graphics.Color
import android.support.v4.content.res.ResourcesCompat
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.charts.HorizontalBarChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IAxisValueFormatter
import net.erikkarlsson.simplesleeptracker.R
import net.erikkarlsson.simplesleeptracker.base.SECONDS_IN_AN_HOUR
import net.erikkarlsson.simplesleeptracker.domain.entity.StatisticComparison
import net.erikkarlsson.simplesleeptracker.feature.statistics.*
import net.erikkarlsson.simplesleeptracker.util.formatHHMM
import net.erikkarlsson.simplesleeptracker.util.midnightOffsetInSeconds
import org.threeten.bp.DayOfWeek
import org.threeten.bp.LocalTime
import org.threeten.bp.format.TextStyle
import timber.log.Timber
import java.util.*
import javax.inject.Inject

class AverageBedTimeChartRenderer @Inject constructor(private val ctx: Context) {

    fun render(avergeBedTimeChart: BarChart, statistics: StatisticComparison) {

        val current = statistics.first
        val averageBedTime = current.averageBedTime

        val rightAxis = avergeBedTimeChart.getAxisRight()
        rightAxis.setEnabled(false)

        val leftAxis = avergeBedTimeChart.axisLeft
        leftAxis.isEnabled = true
        leftAxis.setDrawGridLines(false)
        leftAxis.setDrawAxisLine(false)
        leftAxis.setDrawLabels(false)
        leftAxis.setDrawZeroLine(true)
        leftAxis.setDrawZeroLine(true) // draw a zero line
        leftAxis.setZeroLineColor(Color.RED)
        leftAxis.setZeroLineWidth(0.7f)
        leftAxis.textSize = AXIS_TEXT_SIZE

        val xAxis = avergeBedTimeChart.getXAxis()
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM)
        xAxis.setDrawGridLines(false)
        xAxis.setDrawAxisLine(false)
        xAxis.setTextSize(AXIS_TEXT_SIZE)
        xAxis.setGranularity(1f)
        xAxis.valueFormatter = IAxisValueFormatter { value, _ -> DayOfWeek.of(7 - value.toInt()).getDisplayName(TextStyle.SHORT, Locale.getDefault()) }

        val legend = avergeBedTimeChart.getLegend()
        legend.isEnabled = false

        val yValues = ArrayList<BarEntry>()
        val colors = ArrayList<Int>()

        val averageMidnightOffsetInSeconds = averageBedTime.midnightOffsetInSeconds

        var maxDiff = -99999999
        var minDiff = 99999999

        val green = Color.rgb(110, 190, 102)
        val red = Color.rgb(211, 74, 88)

        for (day in 7 downTo 1) {
            val x = 7 - day.toFloat()

            val dayOfWeekLocalTime = current.averageBedTimeDayOfWeekFor(day)

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

        var maxValue = Math.max(Math.abs(maxDiff), Math.abs(minDiff))

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

        avergeBedTimeChart.setData(data)
        avergeBedTimeChart.setDrawBarShadow(false)
        avergeBedTimeChart.setDrawValueAboveBar(true)
        avergeBedTimeChart.setHighlightFullBarEnabled(false)
        avergeBedTimeChart.setDrawGridBackground(false)
        avergeBedTimeChart.getDescription().setEnabled(false)
        avergeBedTimeChart.setScaleEnabled(false)
        avergeBedTimeChart.setPinchZoom(false)
        avergeBedTimeChart.setTouchEnabled(false)
        avergeBedTimeChart.isDragEnabled = false
        avergeBedTimeChart.description.text = ""
        avergeBedTimeChart.animateY(ANIMATION_DURATION, Easing.EasingOption.EaseOutQuad)
        avergeBedTimeChart.invalidate()
    }
}