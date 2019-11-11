package net.erikkarlsson.simplesleeptracker.features.statistics.item

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.isVisible
import com.airbnb.mvrx.BaseMvRxFragment
import com.airbnb.mvrx.Success
import com.airbnb.mvrx.fragmentViewModel
import com.airbnb.mvrx.withState
import com.github.mikephil.charting.charts.BarChart
import dagger.android.support.AndroidSupportInjection
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.fragment_statistics_item.*
import net.erikkarlsson.simplesleeptracker.R
import net.erikkarlsson.simplesleeptracker.domain.entity.DateRange
import net.erikkarlsson.simplesleeptracker.domain.entity.Sleep
import net.erikkarlsson.simplesleeptracker.domain.entity.StatisticComparison
import net.erikkarlsson.simplesleeptracker.features.statistics.DateRangePair
import net.erikkarlsson.simplesleeptracker.features.statistics.StatisticsFilter
import net.erikkarlsson.simplesleeptracker.features.statistics.chart.AverageTimeChartRenderer
import net.erikkarlsson.simplesleeptracker.features.statistics.chart.SleepDurationChartRenderer
import net.erikkarlsson.simplesleeptracker.util.formatDateDisplayName
import net.erikkarlsson.simplesleeptracker.util.formatHHMM
import net.erikkarlsson.simplesleeptracker.util.formatHoursMinutesSpannable
import net.erikkarlsson.simplesleeptracker.util.formatHoursMinutesWithPrefix
import javax.inject.Inject

data class ChartExtra(val displayValue: Boolean)

class StatisticsItemFragment : BaseMvRxFragment() {

    @Inject
    lateinit var sleepDurationChartRenderer: SleepDurationChartRenderer

    @Inject
    lateinit var averageTimeChartRenderer: AverageTimeChartRenderer

    @Inject
    lateinit var ctx: Context

    @Inject
    lateinit var viewModelFactory: StatisticsItemViewModel.Factory

    private val viewModel: StatisticsItemViewModel by fragmentViewModel()

    private val disposables = CompositeDisposable()

    override fun onAttach(context: Context) {
        AndroidSupportInjection.inject(this)
        super.onAttach(context)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_statistics_item, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        sleepDurationChart.setNoDataText("")
        averageBedTimeChart.setNoDataText("")
        averageWakeUpTimeChart.setNoDataText("")

        val isEmptyState = arguments?.getBoolean(ARGS_IS_EMPTY_STATE) ?: false

        if (isEmptyState) {
            renderEmptyState()
        } else {
            loadStatistics()
        }
    }

    override fun invalidate() = withState(viewModel) { state ->
        render(state)
    }

    private fun render(state: StatisticsItemState) {
        when {
            state.isStatisticsEmpty -> {
                trackedNightsText.text = "0"
                avgDurationText.text = "-"
                sleepDurationChartRenderer.render(sleepDurationChart as BarChart, StatisticComparison.empty())
            }
            state.statistics.complete -> {
                val statistics = state.statistics.invoke() ?: return

                sleepDurationChartRenderer.render(sleepDurationChart as BarChart, statistics)

                with(statistics.first) {
                    if (!isEmpty) {
                        trackedNightsText.text = sleepCount.toString()
                        avgDurationText.text = avgSleepHours.formatHoursMinutesSpannable

                        renderAvgTimeDiff(avgDurationDiffText, statistics.avgSleepDiffHours)
                        renderLongestNight(longestSleep)
                        renderShortestNight(shortestSleep, longestSleep)
                        renderAvgTimeDiff(avgBedDiffText, statistics.avgBedTimeDiffHours)
                        renderAvgTimeDiff(avgWakeUpDiffText, statistics.avgWakeUpTimeDiffHours)
                        renderAverageBedTime(statistics)
                        renderAverageWakeUpTime(statistics)
                    }
                }
            }
        }
    }

    private fun loadStatistics() {
        val dateRangeFirst = arguments?.getParcelable(ARGS_DATE_RANGE_FIRST) as DateRange
        val dateRangeSecond = arguments?.getParcelable(ARGS_DATE_RANGE_SECOND) as DateRange
        val pair = Pair(dateRangeFirst, dateRangeSecond)
        val filter = arguments?.getSerializable(ARGS_STATISTICS_FILTER) as StatisticsFilter

        viewModel.loadStatistics(pair, filter)
    }

    override fun onStop() {
        super.onStop()
        disposables.clear()
    }

    private fun renderEmptyState() {
        val statisticComparison = StatisticComparison.demo()
        val statisticsItemState = StatisticsItemState(Success(statisticComparison))

        sleepDurationLabel.text = String.format("%s [%s]", getString(R.string.sleep_duration), getString(R.string.demo))
        longestNightLabel.text = String.format("%s [%s]", getString(R.string.longest_night), getString(R.string.demo))
        shortestNightLabel.text = String.format("%s [%s]", getString(R.string.shortest_night), getString(R.string.demo))
        averageBedTimeLabel.text = String.format("%s [%s]", getString(R.string.average_bed_time), getString(R.string.demo))
        averageWakeUpTimeLabel.text = String.format("%s [%s]", getString(R.string.average_wake_up_time), getString(R.string.demo))

        render(statisticsItemState)
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
        longestNightDurationText.text = longestSleep.hours.formatHoursMinutesSpannable
        longestNightDateText.text = longestSleep.toDate?.formatDateDisplayName

        val value = if (longestSleep.hours == 0f) {
            0f
        } else {
            1f
        }

        longestNightBar.progress = (value * 100).toInt()
    }

    private fun renderShortestNight(shortestSleep: Sleep, longestSleep: Sleep) {
        shortestNightDurationText.text = shortestSleep.hours.formatHoursMinutesSpannable
        shortestNightDateText.text = shortestSleep.toDate?.formatDateDisplayName

        val value = shortestSleep.hours
        val maxValue = longestSleep.hours

        shortestNightBar.progress = ((value / maxValue) * 100).toInt()
    }

    companion object {
        private const val ARGS_DATE_RANGE_FIRST = "args_date_range_first"
        private const val ARGS_DATE_RANGE_SECOND = "args_date_range_second"
        private const val ARGS_STATISTICS_FILTER = "args_statistics_filter"
        private const val ARGS_IS_EMPTY_STATE = "args_is_empty_state"

        fun newInstance(dataRangePair: DateRangePair,
                        filter: StatisticsFilter,
                        isEmptyState: Boolean = false): StatisticsItemFragment {
            val args = Bundle()
            args.putParcelable(ARGS_DATE_RANGE_FIRST, dataRangePair.first)
            args.putParcelable(ARGS_DATE_RANGE_SECOND, dataRangePair.second)
            args.putSerializable(ARGS_STATISTICS_FILTER, filter)
            args.putBoolean(ARGS_IS_EMPTY_STATE, isEmptyState)

            val fragment = StatisticsItemFragment()
            fragment.arguments = args

            return fragment
        }
    }
}
