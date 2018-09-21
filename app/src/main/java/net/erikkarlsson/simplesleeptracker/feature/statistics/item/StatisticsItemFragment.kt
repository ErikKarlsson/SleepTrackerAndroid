package net.erikkarlsson.simplesleeptracker.feature.statistics.item

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProvider
import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.content.res.ResourcesCompat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.view.isVisible
import dagger.android.support.AndroidSupportInjection
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.fragment_statistics_item.*
import net.erikkarlsson.simplesleeptracker.R
import net.erikkarlsson.simplesleeptracker.di.ViewModelFactory
import net.erikkarlsson.simplesleeptracker.domain.entity.DateRange
import net.erikkarlsson.simplesleeptracker.domain.entity.Sleep
import net.erikkarlsson.simplesleeptracker.domain.entity.StatisticComparison
import net.erikkarlsson.simplesleeptracker.elm.ElmViewModel
import net.erikkarlsson.simplesleeptracker.feature.statistics.DateRangePair
import net.erikkarlsson.simplesleeptracker.feature.statistics.StatisticsFilter
import net.erikkarlsson.simplesleeptracker.feature.statistics.chart.AverageTimeChartRenderer
import net.erikkarlsson.simplesleeptracker.feature.statistics.chart.BasicChartRenderer
import net.erikkarlsson.simplesleeptracker.feature.statistics.chart.SleepDurationChartRenderer
import net.erikkarlsson.simplesleeptracker.util.formatDateDisplayName
import net.erikkarlsson.simplesleeptracker.util.formatHHMM
import net.erikkarlsson.simplesleeptracker.util.formatHoursMinutes
import net.erikkarlsson.simplesleeptracker.util.formatHoursMinutesWithPrefix
import javax.inject.Inject

data class ChartExtra(val displayValue: Boolean)

class StatisticsItemFragment : Fragment() {

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

    companion object {
        private const val ARGS_DATE_RANGE_FIRST = "args_date_range_first"
        private const val ARGS_DATE_RANGE_SECOND = "args_date_range_second"
        private const val ARGS_STATISTICS_FILTER = "args_statistics_filter"

        fun newInstance(dataRangePair: DateRangePair,
                        filter: StatisticsFilter): StatisticsItemFragment {
            val args = Bundle()
            args.putParcelable(ARGS_DATE_RANGE_FIRST, dataRangePair.first)
            args.putParcelable(ARGS_DATE_RANGE_SECOND, dataRangePair.second)
            args.putSerializable(ARGS_STATISTICS_FILTER, filter)

            val fragment = StatisticsItemFragment()
            fragment.arguments = args

            return fragment
        }
    }

    private val viewModel: StatisticsItemViewModel by lazy {
        ViewModelProvider(this, viewModelFactory).get(StatisticsItemViewModel::class.java)
    }

    private val disposables = CompositeDisposable()

    override fun onAttach(context: Context?) {
        AndroidSupportInjection.inject(this)
        super.onAttach(context)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_statistics_item, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val dateRangeFirst = arguments?.getParcelable(ARGS_DATE_RANGE_FIRST) as DateRange
        val dateRangeSecond = arguments?.getParcelable(ARGS_DATE_RANGE_SECOND) as DateRange
        val pair = Pair(dateRangeFirst, dateRangeSecond)
        val filter = arguments?.getSerializable(ARGS_STATISTICS_FILTER) as StatisticsFilter
        val loadStatistics = LoadStatistics(pair, filter)

        viewModel.dispatch(loadStatistics)
        viewModel.state().observe(this, Observer { render(it) })
    }

    override fun onStop() {
        super.onStop()
        disposables.clear()
    }

    private fun render(state: StatisticsItemState?) {
        state?.let {
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
}

class StatisticsItemViewModel @Inject constructor(statisticsItemComponent: StatisticsItemComponent) :
        ElmViewModel<StatisticsItemState, StatisticsItemMsg, StatisticsItemCmd>(statisticsItemComponent)
