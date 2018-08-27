package net.erikkarlsson.simplesleeptracker.feature.statistics

import io.reactivex.Observable
import io.reactivex.Single
import net.erikkarlsson.simplesleeptracker.domain.entity.StatisticComparison
import net.erikkarlsson.simplesleeptracker.domain.task.ObservableTask
import net.erikkarlsson.simplesleeptracker.elm.*
import net.erikkarlsson.simplesleeptracker.feature.statistics.domain.StatisticComparisonOverallTask
import net.erikkarlsson.simplesleeptracker.feature.statistics.domain.StatisticComparisonWeekTask
import javax.inject.Inject

class StatisticsComponent @Inject constructor(private val statisticsSubscription: StatisticsSubscription)
    : Component<StatisticsState, StatisticsMsg, StatisticsCmd> {

    override fun call(cmd: StatisticsCmd): Single<StatisticsMsg> =
        Single.just(NoOp)

    override fun initState(): StatisticsState = StatisticsState.empty()

    override fun subscriptions(): List<Sub<StatisticsState, StatisticsMsg>> = listOf(statisticsSubscription)

    override fun update(msg: StatisticsMsg, prevState: StatisticsState): Pair<StatisticsState, StatisticsCmd?> = when (msg) {
        is StatisticsLoaded -> prevState.copy(statistics = msg.statistics).noCmd()
        is StatisticsFilterSelected -> prevState.copy(statisticFilter = msg.filter).noCmd()
        NoOp -> prevState.noCmd()
    }

}

// State
data class StatisticsState(val statistics: StatisticComparison,
                           val statisticFilter: StatisticsFilter) : State {

    companion object {
        fun empty() = StatisticsState(StatisticComparison.empty(), StatisticsFilter.OVERALL)
    }
}

// Subscription
class StatisticsSubscription @Inject constructor(private val statisticComparisonOverallTask: StatisticComparisonOverallTask,
                                                 private val statisticComparisonWeekTask: StatisticComparisonWeekTask) : StatefulSub<StatisticsState, StatisticsMsg>() {
    override fun invoke(state: StatisticsState): Observable<StatisticsMsg> {
        if (state.statisticFilter == StatisticsFilter.OVERALL) {
            return statisticComparisonOverallTask.execute(ObservableTask.None())
                    .map { StatisticsLoaded(it) }
        } else {
            return statisticComparisonWeekTask.execute(ObservableTask.None())
                    .map { StatisticsLoaded(it) }
        }
    }

    override fun isDistinct(s1: StatisticsState, s2: StatisticsState): Boolean {
        return s1.statisticFilter != s2.statisticFilter
    }
}

// Msg
sealed class StatisticsMsg : Msg

object NoOp : StatisticsMsg()

data class StatisticsFilterSelected(val filter: StatisticsFilter) : StatisticsMsg()
data class StatisticsLoaded(val statistics: StatisticComparison) : StatisticsMsg()

// Cmd
sealed class StatisticsCmd : Cmd