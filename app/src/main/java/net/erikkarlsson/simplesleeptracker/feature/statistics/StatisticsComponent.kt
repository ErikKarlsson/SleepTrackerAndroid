package net.erikkarlsson.simplesleeptracker.feature.statistics

import io.reactivex.Observable
import io.reactivex.Single
import net.erikkarlsson.simplesleeptracker.domain.entity.Sleep
import net.erikkarlsson.simplesleeptracker.domain.entity.StatisticComparison
import net.erikkarlsson.simplesleeptracker.domain.task.CompletableTask.None
import net.erikkarlsson.simplesleeptracker.domain.task.ObservableTask
import net.erikkarlsson.simplesleeptracker.domain.task.sleep.GetCurrentSleepTask
import net.erikkarlsson.simplesleeptracker.domain.task.sleep.ToggleSleepTask
import net.erikkarlsson.simplesleeptracker.domain.task.statistics.StatisticComparisonOverallTask
import net.erikkarlsson.simplesleeptracker.domain.task.statistics.StatisticComparisonWeekTask
import net.erikkarlsson.simplesleeptracker.elm.Cmd
import net.erikkarlsson.simplesleeptracker.elm.Component
import net.erikkarlsson.simplesleeptracker.elm.Msg
import net.erikkarlsson.simplesleeptracker.elm.State
import net.erikkarlsson.simplesleeptracker.elm.StatefulSub
import net.erikkarlsson.simplesleeptracker.elm.StatelessSub
import net.erikkarlsson.simplesleeptracker.elm.Sub
import net.erikkarlsson.simplesleeptracker.feature.statistics.StatisticsCmd.ToggleSleepCmd
import javax.inject.Inject

class StatisticsComponent @Inject constructor(private val toggleSleepTask: ToggleSleepTask,
                                              private val sleepSubscription: SleepSubscription,
                                              private val statisticsSubscription: StatisticsSubscription)
    : Component<StatisticsState, StatisticsMsg, StatisticsCmd> {

    override fun call(cmd: StatisticsCmd): Single<StatisticsMsg> = when (cmd) {
        ToggleSleepCmd -> toggleSleepTask.execute(None()).toSingleDefault(NoOp)
    }

    override fun initState(): StatisticsState = StatisticsState.empty()

    override fun subscriptions(): List<Sub<StatisticsState, StatisticsMsg>> = listOf(sleepSubscription, statisticsSubscription)

    override fun update(msg: StatisticsMsg, prevState: StatisticsState): Pair<StatisticsState, StatisticsCmd?> = when (msg) {
        NoOp -> prevState.noCmd()
        ToggleSleepClicked -> prevState withCmd ToggleSleepCmd
        is CurrentSleepLoaded -> prevState.copy(isSleeping = msg.sleep.isSleeping).noCmd()
        is StatisticsLoaded -> prevState.copy(statistics = msg.statistics).noCmd()
        is StatisticsFilterSelected -> prevState.copy(statisticFilter = msg.filter).noCmd()
    }

}

// State
data class StatisticsState(val isSleeping: Boolean,
                           val statistics: StatisticComparison,
                           val statisticFilter: StatisticsFilter) : State {

    companion object {
        fun empty() = StatisticsState(false, StatisticComparison.empty(), StatisticsFilter.OVERALL)
    }
}

// Subscription
class SleepSubscription @Inject constructor(private val getCurrentSleepTask: GetCurrentSleepTask) : StatelessSub<StatisticsState, StatisticsMsg>() {

    override fun invoke(): Observable<StatisticsMsg> =
            getCurrentSleepTask.execute(ObservableTask.None())
                    .map { CurrentSleepLoaded(it) }
}

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

object ToggleSleepClicked : StatisticsMsg()
data class StatisticsFilterSelected(val filter: StatisticsFilter) : StatisticsMsg()
data class CurrentSleepLoaded(val sleep: Sleep) : StatisticsMsg()
data class StatisticsLoaded(val statistics: StatisticComparison) : StatisticsMsg()
object NoOp : StatisticsMsg()

// Cmd
sealed class StatisticsCmd : Cmd {
    object ToggleSleepCmd : StatisticsCmd()
}