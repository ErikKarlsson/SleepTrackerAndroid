package net.erikkarlsson.simplesleeptracker.statistics

import android.content.Context
import android.content.Intent
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import net.erikkarlsson.simplesleeptracker.domain.Sleep
import net.erikkarlsson.simplesleeptracker.domain.SleepDataSource
import net.erikkarlsson.simplesleeptracker.domain.Statistics
import net.erikkarlsson.simplesleeptracker.domain.StatisticsDataSource
import net.erikkarlsson.simplesleeptracker.elm.*
import net.erikkarlsson.simplesleeptracker.sleepappwidget.SleepAppWidgetProvider
import net.erikkarlsson.simplesleeptracker.sleepappwidget.WidgetConstants
import net.erikkarlsson.simplesleeptracker.statistics.LoadStatisticsResult.LoadStatisticsFailure
import net.erikkarlsson.simplesleeptracker.statistics.LoadStatisticsResult.LoadStatisticsSuccess
import net.erikkarlsson.simplesleeptracker.statistics.StatisticsCmd.LoadStatisticsAction
import net.erikkarlsson.simplesleeptracker.statistics.StatisticsCmd.ToggleSleepAction
import net.erikkarlsson.simplesleeptracker.util.SchedulerProvider
import javax.inject.Inject

class StatisticsComponent @Inject constructor(private val loadStatistics: LoadStatistics,
                                              private val toggleSleep: ToggleSleep,
                                              private val sleepSubscription: SleepSubscription)
    : Component<StatisticsState, StatisticsMsg, StatisticsCmd> {

    override fun call(cmd: StatisticsCmd): Single<StatisticsMsg> = when (cmd) {
        is LoadStatisticsAction -> loadStatistics.task()
        ToggleSleepAction -> toggleSleep.task()
    }

    override fun initState(): StatisticsState = StatisticsState.empty()

    override fun subscriptions(): List<Sub<StatisticsState, StatisticsMsg>> = listOf(sleepSubscription)

    override fun update(msg: StatisticsMsg, prevState: StatisticsState): Pair<StatisticsState, StatisticsCmd?> = when (msg) {
        is LoadStatisticsSuccess -> prevState.copy(statistics = msg.statistics).noCmd()
        is LoadStatisticsFailure -> prevState.copy(statistics = Statistics.empty()).noCmd()
        is InitialIntent -> prevState withCmd LoadStatisticsAction
        ToggleSleepIntent -> prevState withCmd ToggleSleepAction
        NoOp -> prevState.noCmd()
        is SleepList -> prevState.copy(sleepList = msg.sleepList).noCmd()
    }

}

// State
data class StatisticsState(val statistics: Statistics,
                           val sleepList: List<Sleep>) : State {
    companion object {
        fun empty() = StatisticsState(Statistics.empty(), listOf())
    }
}

class SleepSubscription @Inject constructor(private val sleepRepository: SleepDataSource)
    : StatelessSub<StatisticsState, StatisticsMsg>() {

    override fun invoke(): Observable<StatisticsMsg> {
        return sleepRepository.getSleep()
            .subscribeOn(Schedulers.io())
            .map { SleepList(it) }
    }

}

// Msg
sealed class StatisticsMsg : Msg

object InitialIntent : StatisticsMsg()
object ToggleSleepIntent : StatisticsMsg()
data class SleepList(val sleepList: List<Sleep>) : StatisticsMsg()
object NoOp : StatisticsMsg()

sealed class LoadStatisticsResult : StatisticsMsg() {
    data class LoadStatisticsSuccess(val statistics: Statistics) : LoadStatisticsResult()
    data class LoadStatisticsFailure(val error: Throwable) : LoadStatisticsResult()
}

// Cmd
sealed class StatisticsCmd : Cmd {
    object LoadStatisticsAction : StatisticsCmd()
    object ToggleSleepAction : StatisticsCmd()
}

// Tasks
class LoadStatistics @Inject constructor(
        private val statisticsRepository: StatisticsDataSource,
        private val schedulerProvider: SchedulerProvider) {

    internal fun task(): Single<StatisticsMsg> =
            statisticsRepository.getStatistics()
                .map { LoadStatisticsSuccess(it) }
                .cast(StatisticsMsg::class.java)
                .onErrorReturn(LoadStatisticsResult::LoadStatisticsFailure)
                .subscribeOn(schedulerProvider.io())
}

class ToggleSleep @Inject constructor(private val context: Context) {
    internal fun task(): Single<StatisticsMsg> {
        val intent = Intent(context, SleepAppWidgetProvider::class.java)
        intent.action = WidgetConstants.ACTION_SIMPLEAPPWIDGET_TOGGLE
        context.sendBroadcast(intent)
        return Single.just(NoOp)
    }
}