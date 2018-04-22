package net.erikkarlsson.simplesleeptracker.feature.statistics

import com.google.common.collect.ImmutableList
import io.reactivex.Observable
import io.reactivex.Single
import net.erikkarlsson.simplesleeptracker.domain.SleepDataSource
import net.erikkarlsson.simplesleeptracker.domain.entity.Sleep
import net.erikkarlsson.simplesleeptracker.domain.entity.StatisticComparison
import net.erikkarlsson.simplesleeptracker.domain.task.StatisticComparisonOverallTask
import net.erikkarlsson.simplesleeptracker.domain.task.StatisticComparisonWeekTask
import net.erikkarlsson.simplesleeptracker.domain.task.ToggleSleepTask
import net.erikkarlsson.simplesleeptracker.elm.*
import net.erikkarlsson.simplesleeptracker.feature.statistics.StatisticsCmd.ToggleSleepCmd
import javax.inject.Inject

class StatisticsComponent @Inject constructor(private val toggleSleepTask: ToggleSleepTask,
                                              private val sleepSubscription: SleepSubscription,
                                              private val statisticsSubscription: StatisticsSubscription)
    : Component<StatisticsState, StatisticsMsg, StatisticsCmd> {

    override fun call(cmd: StatisticsCmd): Single<StatisticsMsg> = when (cmd) {
        ToggleSleepCmd -> toggleSleepTask.execute().toSingleDefault(NoOp)
    }

    override fun initState(): StatisticsState = StatisticsState.empty()

    override fun subscriptions(): List<Sub<StatisticsState, StatisticsMsg>> = listOf(sleepSubscription, statisticsSubscription)

    override fun update(msg: StatisticsMsg, prevState: StatisticsState): Pair<StatisticsState, StatisticsCmd?> = when (msg) {
        NoOp -> prevState.noCmd()
        ToggleSleepClicked -> prevState withCmd ToggleSleepCmd
        is SleepLoaded -> prevState.copy(sleepList = msg.sleepList).noCmd()
        is CurrentSleepLoaded -> prevState.copy(isSleeping = msg.sleep.isSleeping).noCmd()
        is StatisticsLoaded -> prevState.copy(statistics = msg.statistics).noCmd()
        is StatisticsFilterSelected -> prevState.copy(statisticFilter = msg.filter).noCmd()
    }

}

// State
data class StatisticsState(val isSleeping: Boolean,
                           val statistics: StatisticComparison,
                           val sleepList: ImmutableList<Sleep>,
                           val statisticFilter: StatisticsFilter) : State {

    val isListEmpty get(): Boolean = sleepList.isEmpty()

    companion object {
        fun empty() = StatisticsState(false, StatisticComparison.empty(), ImmutableList.of(), StatisticsFilter.OVERALL)
    }
}

// Subscription
class SleepSubscription @Inject constructor(private val sleepRepository: SleepDataSource) : StatelessSub<StatisticsState, StatisticsMsg>() {

    override fun invoke(): Observable<StatisticsMsg> =
            Observable.merge(
                    sleepRepository.getSleep().map { SleepLoaded(it) },
                    sleepRepository.getCurrent().map { CurrentSleepLoaded(it) }
            )
}

class StatisticsSubscription @Inject constructor(private val statisticComparisonOverallTask: StatisticComparisonOverallTask,
                                                 private val statisticComparisonWeekTask: StatisticComparisonWeekTask) : StatefulSub<StatisticsState, StatisticsMsg>() {
    override fun invoke(state: StatisticsState): Observable<StatisticsMsg> {
        if (state.statisticFilter == StatisticsFilter.OVERALL) {
            return statisticComparisonOverallTask.execute().map { StatisticsLoaded(it) }
        } else {
            return statisticComparisonWeekTask.execute().map { StatisticsLoaded(it) }
        }
    }

    override fun isDistinct(s1: StatisticsState, s2: StatisticsState): Boolean {
        return s1.statisticFilter != s2.statisticFilter
    }
}

// Msg
sealed class StatisticsMsg : Msg

object ToggleSleepClicked : StatisticsMsg()
data class StatisticsFilterSelected(val filter: StatisticsFilter) : StatisticsMsg()
data class SleepLoaded(val sleepList: ImmutableList<Sleep>) : StatisticsMsg()
data class CurrentSleepLoaded(val sleep: Sleep) : StatisticsMsg()
data class StatisticsLoaded(val statistics: StatisticComparison) : StatisticsMsg()
object NoOp : StatisticsMsg()

// Cmd
sealed class StatisticsCmd : Cmd {
    object ToggleSleepCmd : StatisticsCmd()
}