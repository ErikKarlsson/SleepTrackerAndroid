package net.erikkarlsson.simplesleeptracker.statistics

import android.content.Context
import android.content.Intent
import io.reactivex.Observable
import io.reactivex.Single
import net.erikkarlsson.simplesleeptracker.domain.NetworkProvider
import net.erikkarlsson.simplesleeptracker.domain.Statistics
import net.erikkarlsson.simplesleeptracker.domain.StatisticsDataSource
import net.erikkarlsson.simplesleeptracker.elm.*
import net.erikkarlsson.simplesleeptracker.sleepappwidget.SleepAppWidgetProvider
import net.erikkarlsson.simplesleeptracker.sleepappwidget.WidgetConstants
import net.erikkarlsson.simplesleeptracker.statistics.StatisticsCmd.LoadStatisticsAction
import net.erikkarlsson.simplesleeptracker.statistics.StatisticsCmd.ToggleSleepAction
import net.erikkarlsson.simplesleeptracker.statistics.StatisticsMsg.*
import net.erikkarlsson.simplesleeptracker.statistics.StatisticsMsg.LoadStatisticsResult.LoadStatisticsFailure
import net.erikkarlsson.simplesleeptracker.statistics.StatisticsMsg.LoadStatisticsResult.LoadStatisticsSuccess
import net.erikkarlsson.simplesleeptracker.util.SchedulerProvider
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class StatisticsComponent @Inject constructor(private val loadStatistics: LoadStatistics,
                                              private val toggleSleep: ToggleSleep,
                                              private val networkSubscription: NetworkSubscription)
    : Component<StatisticsState, StatisticsMsg, StatisticsCmd> {

    override fun call(cmd: StatisticsCmd): Single<StatisticsMsg> = when (cmd) {
        is LoadStatisticsAction -> loadStatistics.task()
        ToggleSleepAction -> toggleSleep.task()
    }

    override fun initState(): StatisticsState = StatisticsState.empty()

    override fun subscriptions(): List<Sub<StatisticsState, StatisticsMsg>> = listOf(networkSubscription, TickerSubscription())

    override fun update(msg: StatisticsMsg, prevState: StatisticsState): Pair<StatisticsState, StatisticsCmd?> = when (msg) {
        is LoadStatisticsSuccess -> prevState.copy(statistics = msg.statistics).noCmd()
        is LoadStatisticsFailure -> prevState.copy(statistics = Statistics.empty()).noCmd()
        is InitialIntent -> prevState withCmd LoadStatisticsAction
        is NetworkStatus -> prevState.copy(isConnectedToInternet = msg.isConnectedToInternet).noCmd()
        Tick -> prevState.copy(count = (prevState.count + 1)).noCmd()
        ToggleSleepMsg -> prevState withCmd ToggleSleepAction
        NoOp -> prevState.noCmd()
    }

}

// Subscription
class NetworkSubscription @Inject constructor(private val networkProvider: NetworkProvider)
    : StatelessSub<StatisticsState, StatisticsMsg>() {

    override fun invoke(): Observable<StatisticsMsg> {
        return networkProvider.isConnectedToNetwork().map { NetworkStatus(it) }
    }

}

class TickerSubscription : StatefulSub<StatisticsState, StatisticsMsg>() {
    override fun invoke(state: StatisticsState): Observable<StatisticsMsg> = when {
        state.isConnectedToInternet -> Observable.interval(1, TimeUnit.SECONDS).map { Tick }
        else -> Observable.empty()
    }

    override fun isDistinct(s1: StatisticsState, s2: StatisticsState) = s1.isConnectedToInternet != s2.isConnectedToInternet
}

// State
data class StatisticsState(val statistics: Statistics,
                           val isConnectedToInternet: Boolean,
                           val count: Int) : State {
    companion object {
        fun empty() = StatisticsState(Statistics.empty(), true, 0)
    }
}

// Msg
sealed class StatisticsMsg : Msg {
    object InitialIntent : StatisticsMsg()
    object ToggleSleepMsg : StatisticsMsg()
    data class NetworkStatus(val isConnectedToInternet: Boolean) : StatisticsMsg()
    object Tick : StatisticsMsg()
    object NoOp : StatisticsMsg()

    sealed class LoadStatisticsResult : StatisticsMsg() {
        data class LoadStatisticsSuccess(val statistics: Statistics) : LoadStatisticsResult()
        data class LoadStatisticsFailure(val error: Throwable) : LoadStatisticsResult()
    }
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
                .map { StatisticsMsg.LoadStatisticsResult.LoadStatisticsSuccess(it) }
                .cast(StatisticsMsg::class.java)
                .onErrorReturn(StatisticsMsg.LoadStatisticsResult::LoadStatisticsFailure)
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