package net.erikkarlsson.simplesleeptracker.feature.statistics.item

import io.reactivex.Observable
import io.reactivex.Single
import net.erikkarlsson.simplesleeptracker.domain.entity.DateRange
import net.erikkarlsson.simplesleeptracker.domain.entity.StatisticComparison
import net.erikkarlsson.simplesleeptracker.domain.task.ObservableTask
import net.erikkarlsson.simplesleeptracker.elm.*
import net.erikkarlsson.simplesleeptracker.feature.statistics.DateRangePair
import net.erikkarlsson.simplesleeptracker.feature.statistics.StatisticsFilter
import net.erikkarlsson.simplesleeptracker.feature.statistics.StatisticsFilter.OVERALL
import net.erikkarlsson.simplesleeptracker.feature.statistics.domain.StatisticComparisonTask
import net.erikkarlsson.simplesleeptracker.feature.statistics.domain.StatisticOverallTask
import javax.inject.Inject

class StatisticsItemComponent @Inject constructor(private val statisticsSubscription: StatisticsSubscription)
    : Component<StatisticsItemState, StatisticsItemMsg, StatisticsItemCmd> {

    override fun call(cmd: StatisticsItemCmd): Single<StatisticsItemMsg> =
            Single.just(DoNothing)

    override fun initState(): StatisticsItemState = StatisticsItemState.empty()

    override fun subscriptions(): List<Sub<StatisticsItemState, StatisticsItemMsg>> = listOf(statisticsSubscription)

    override fun update(msg: StatisticsItemMsg, prevState: StatisticsItemState): Pair<StatisticsItemState, StatisticsItemCmd?> =
            when (msg) {
                is LoadStatistics -> prevState.copy(filter = msg.filter, dataRangePair = msg.dataRangePair).noCmd()
                is StatisticsLoaded -> {
                    prevState.copy(statistics = msg.statistics,
                            LoadCount = (prevState.LoadCount + 1)).noCmd()
                }
                DoNothing -> prevState.noCmd()
            }
}

// State
data class StatisticsItemState(val LoadCount: Int,
                               val statistics: StatisticComparison,
                               val filter: StatisticsFilter,
                               val dataRangePair: DateRangePair) : State {

    val isStatisticsEmpty
        get() = statistics == StatisticComparison.empty()

    // Don't rendering while loading to avoid flicker.
    // First emitted result is always empty.
    val isLoading = LoadCount < 2

    companion object {
        fun empty() = StatisticsItemState(0,
                StatisticComparison.empty(),
                OVERALL,
                DateRange.empty() to DateRange.empty())
    }
}

// Subscription
class StatisticsSubscription @Inject constructor(private val statisticOverallTask: StatisticOverallTask,
                                                 private val statisticComparisonTask: StatisticComparisonTask) : StatefulSub<StatisticsItemState, StatisticsItemMsg>() {
    override fun invoke(state: StatisticsItemState): Observable<StatisticsItemMsg> =
            when {
                state.filter == OVERALL -> {
                    statisticOverallTask.execute(ObservableTask.None())
                            .map { StatisticsLoaded(it) }
                }
                else -> {
                    val params = StatisticComparisonTask.Params(state.dataRangePair)
                    statisticComparisonTask.execute(params)
                            .map { StatisticsLoaded(it) }
                }
            }

    override fun isDistinct(s1: StatisticsItemState, s2: StatisticsItemState): Boolean {
        return s1.filter != s2.filter
    }
}

// Msg
sealed class StatisticsItemMsg : Msg

data class LoadStatistics(val dataRangePair: DateRangePair, val filter: StatisticsFilter) : StatisticsItemMsg()

object DoNothing : StatisticsItemMsg()

data class StatisticsLoaded(val statistics: StatisticComparison) : StatisticsItemMsg()

// Cmd
sealed class StatisticsItemCmd : Cmd