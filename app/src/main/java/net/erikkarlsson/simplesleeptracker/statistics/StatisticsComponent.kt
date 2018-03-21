package net.erikkarlsson.simplesleeptracker.statistics

import io.reactivex.Observable
import io.reactivex.Single
import net.erikkarlsson.simplesleeptracker.domain.*
import net.erikkarlsson.simplesleeptracker.elm.*
import net.erikkarlsson.simplesleeptracker.statistics.StatisticsCmd.ToggleSleepCmd
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
        ToggleSleepClicked -> prevState withCmd ToggleSleepCmd
        is SleepLoaded -> prevState.copy(sleepList = msg.sleepList).noCmd()
        is StatisticsLoaded -> prevState.copy(statistics = msg.statistics).noCmd()
        NoOp -> prevState.noCmd()
    }

}

// State
data class StatisticsState(val statistics: StatisticComparison,
                           val sleepList: List<Sleep>) : State {

    companion object {
        fun empty() = StatisticsState(StatisticComparison.empty(), listOf())
    }
}

// Subscription
class SleepSubscription @Inject constructor(private val sleepRepository: SleepDataSource) : StatelessSub<StatisticsState, StatisticsMsg>() {

    override fun invoke(): Observable<StatisticsMsg> = sleepRepository.getSleep().map { SleepLoaded(it) }
}

class StatisticsSubscription @Inject constructor(private val statisticComparisonTask: StatisticComparisonTask) : StatelessSub<StatisticsState, StatisticsMsg>() {

    override fun invoke(): Observable<StatisticsMsg> = statisticComparisonTask.execute().map { StatisticsLoaded(it) }
}

// Msg
sealed class StatisticsMsg : Msg

object ToggleSleepClicked : StatisticsMsg()
data class SleepLoaded(val sleepList: List<Sleep>) : StatisticsMsg()
data class StatisticsLoaded(val statistics: StatisticComparison) : StatisticsMsg()
object NoOp : StatisticsMsg()

// Cmd
sealed class StatisticsCmd : Cmd {
    object ToggleSleepCmd : StatisticsCmd()
}