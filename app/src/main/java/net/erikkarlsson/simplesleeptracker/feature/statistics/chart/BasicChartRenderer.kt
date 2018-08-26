package net.erikkarlsson.simplesleeptracker.feature.statistics.chart

import android.content.Context
import android.support.v4.content.res.ResourcesCompat
import android.view.ContextThemeWrapper
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import net.erikkarlsson.simplesleeptracker.R
import net.erikkarlsson.simplesleeptracker.feature.statistics.ANIMATION_DURATION
import java.util.ArrayList
import javax.inject.Inject

class BasicChartRenderer @Inject constructor(private val ctx: Context) {

    fun render(barChart: BarChart, value: Float, maxValue: Float) {
        val pinkValues = ArrayList<BarEntry>()
        pinkValues.add(BarEntry(1f, value))

        val set1 = BarDataSet(pinkValues, "")
        set1.color = ResourcesCompat.getColor(ctx.resources, R.color.colorAccent, null)

        val data = BarData(set1)
        data.barWidth = 1f
        data.setDrawValues(false)

        val leftAxis = barChart.axisLeft
        leftAxis.isEnabled = false
        leftAxis.axisMinimum = 0f
        leftAxis.axisMaximum = maxValue

        val rightAxis = barChart.axisRight
        rightAxis.isEnabled = false

        val xAxis = barChart.xAxis
        xAxis.isEnabled = false

        val legend = barChart.legend
        legend.isEnabled = false

        barChart.data = data
        barChart.setScaleEnabled(false)
        barChart.setTouchEnabled(false)
        barChart.isDragEnabled = false
        barChart.setDrawBarShadow(true)
        barChart.description.text = ""
        barChart.setViewPortOffsets(0f, 0f, 0f, 0f)
        barChart.animateY(ANIMATION_DURATION, Easing.EasingOption.EaseOutQuad)
        barChart.setBackgroundColor(ResourcesCompat.getColor(ctx.resources,
                R.color.gray,
                null))
        barChart.invalidate()
    }
}