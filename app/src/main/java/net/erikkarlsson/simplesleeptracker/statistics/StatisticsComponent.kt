package net.erikkarlsson.simplesleeptracker.statistics

import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import net.erikkarlsson.simplesleeptracker.base.ToggleSleepTaskNew
import net.erikkarlsson.simplesleeptracker.data.StatisticsRepository
import net.erikkarlsson.simplesleeptracker.domain.Sleep
import net.erikkarlsson.simplesleeptracker.domain.SleepDataSource
import net.erikkarlsson.simplesleeptracker.domain.Statistics
import net.erikkarlsson.simplesleeptracker.elm.*
import net.erikkarlsson.simplesleeptracker.statistics.StatisticsCmd.ToggleSleepCmd
import javax.inject.Inject

class StatisticsComponent @Inject constructor(private val toggleSleepTaskNew: ToggleSleepTaskNew,
                                              private val sleepSubscription: SleepSubscription,
                                              private val statisticsSubscription: StatisticsSubscription)
    : Component<StatisticsState, StatisticsMsg, StatisticsCmd> {

    override fun call(cmd: StatisticsCmd): Single<StatisticsMsg> = when (cmd) {
        ToggleSleepCmd -> toggleSleepTaskNew.execute().toSingleDefault(NoOp)
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
data class StatisticsState(val statistics: Statistics,
                           val sleepList: List<Sleep>) : State {
    companion object {
        fun empty() = StatisticsState(Statistics.empty(), listOf())
    }
}

// Subscription
class SleepSubscription @Inject constructor(private val sleepRepository: SleepDataSource)
    : StatelessSub<StatisticsState, StatisticsMsg>() {

    override fun invoke(): Observable<StatisticsMsg> {
        return sleepRepository.getSleep()
            .subscribeOn(Schedulers.io())
            .map { SleepLoaded(it) }
    }

}

class StatisticsSubscription @Inject constructor(private val statisticsRepository: StatisticsRepository)
    : StatelessSub<StatisticsState, StatisticsMsg>() {

    override fun invoke(): Observable<StatisticsMsg> {
        return statisticsRepository.getStatistics()
            .subscribeOn(Schedulers.io())
            .map { StatisticsLoaded(it) }
    }

}

// Msg
sealed class StatisticsMsg : Msg

object ToggleSleepClicked : StatisticsMsg()
data class SleepLoaded(val sleepList: List<Sleep>) : StatisticsMsg()
data class StatisticsLoaded(val statistics: Statistics) : StatisticsMsg()
object NoOp : StatisticsMsg()

// Cmd
sealed class StatisticsCmd : Cmd {
    object ToggleSleepCmd : StatisticsCmd()
}