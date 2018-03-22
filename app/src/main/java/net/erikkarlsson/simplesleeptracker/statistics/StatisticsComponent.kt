package net.erikkarlsson.simplesleeptracker.statistics

import com.google.common.collect.ImmutableList
import io.reactivex.Observable
import io.reactivex.Single
import net.erikkarlsson.simplesleeptracker.domain.SleepDataSource
import net.erikkarlsson.simplesleeptracker.domain.entity.Sleep
import net.erikkarlsson.simplesleeptracker.domain.entity.StatisticComparison
import net.erikkarlsson.simplesleeptracker.domain.task.StatisticComparisonTask
import net.erikkarlsson.simplesleeptracker.domain.task.ToggleSleepTask
import net.erikkarlsson.simplesleeptracker.elm.*
import net.erikkarlsson.simplesleeptracker.statistics.StatisticsCmd.ToggleSleepCmd
import javax.inject.Inject

class StatisticsComponent @Inject constructor(private val toggleSleepTask: ToggleSleepTask,
                                              private val statisticsDataSubscription: StatisticsDataSubscription)
    : Component<StatisticsState, StatisticsMsg, StatisticsCmd> {

    override fun call(cmd: StatisticsCmd): Single<StatisticsMsg> = when (cmd) {
        ToggleSleepCmd -> toggleSleepTask.execute().toSingleDefault(NoOp)
    }

    override fun initState(): StatisticsState = StatisticsState.empty()

    override fun subscriptions(): List<Sub<StatisticsState, StatisticsMsg>> = listOf(statisticsDataSubscription)

    override fun update(msg: StatisticsMsg, prevState: StatisticsState): Pair<StatisticsState, StatisticsCmd?> = when (msg) {
        ToggleSleepClicked -> prevState withCmd ToggleSleepCmd
        is SleepLoaded -> prevState.copy(sleepList = msg.sleepList).noCmd()
        is CurrentSleepLoaded -> prevState.copy(isSleeping = msg.sleep.isSleeping).noCmd()
        is StatisticsLoaded -> prevState.copy(statistics = msg.statistics).noCmd()
        NoOp -> prevState.noCmd()
    }

}

// State
data class StatisticsState(val isSleeping: Boolean,
                           val statistics: StatisticComparison,
                           val sleepList: ImmutableList<Sleep>) : State {

    val isListEmpty get(): Boolean = sleepList.isEmpty()

    companion object {
        fun empty() = StatisticsState(false, StatisticComparison.empty(), ImmutableList.of())
    }
}

// Subscription
class StatisticsDataSubscription @Inject constructor(private val sleepRepository: SleepDataSource,
                                                     private val statisticComparisonTask: StatisticComparisonTask) : StatelessSub<StatisticsState, StatisticsMsg>() {

    override fun invoke(): Observable<StatisticsMsg> =
            Observable.merge(
                    sleepRepository.getSleep().map { SleepLoaded(it) },
                    sleepRepository.getCurrent().map { CurrentSleepLoaded(it) },
                    statisticComparisonTask.execute().map { StatisticsLoaded(it) }
            )
}

// Msg
sealed class StatisticsMsg : Msg

object ToggleSleepClicked : StatisticsMsg()
data class SleepLoaded(val sleepList: ImmutableList<Sleep>) : StatisticsMsg()
data class CurrentSleepLoaded(val sleep: Sleep) : StatisticsMsg()
data class StatisticsLoaded(val statistics: StatisticComparison) : StatisticsMsg()
object NoOp : StatisticsMsg()

// Cmd
sealed class StatisticsCmd : Cmd {
    object ToggleSleepCmd : StatisticsCmd()
}