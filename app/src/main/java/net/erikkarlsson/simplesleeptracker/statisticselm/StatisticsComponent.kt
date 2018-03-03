package net.erikkarlsson.simplesleeptracker.statisticselm

import io.reactivex.Observable
import io.reactivex.Single
import net.erikkarlsson.simplesleeptracker.domain.NetworkProvider
import net.erikkarlsson.simplesleeptracker.domain.Statistics
import net.erikkarlsson.simplesleeptracker.elm.*
import net.erikkarlsson.simplesleeptracker.statisticselm.StatisticsMsg.InitialIntent
import net.erikkarlsson.simplesleeptracker.statisticselm.StatisticsMsg.LoadStatisticsResult.LoadStatisticsFailure
import net.erikkarlsson.simplesleeptracker.statisticselm.StatisticsMsg.LoadStatisticsResult.LoadStatisticsSuccess
import net.erikkarlsson.simplesleeptracker.statisticselm.StatisticsMsg.NetworkStatus
import net.erikkarlsson.simplesleeptracker.statisticselm.task.LoadStatistics
import javax.inject.Inject

class StatisticsComponent @Inject constructor(private val loadStatistics: LoadStatistics,
                                              private val networkSubscription: NetworkSubscription)
    : Component<StatisticsState, StatisticsMsg, StatisticsCmd> {

    override fun call(cmd: StatisticsCmd): Single<StatisticsMsg> = when (cmd) {
        is StatisticsCmd.LoadStatisticsAction -> loadStatistics.task()
    }

    override fun initState(): StatisticsState = StatisticsState.empty()

    override fun subscriptions(): List<Sub<StatisticsState, StatisticsMsg>> = listOf(networkSubscription)

    override fun update(msg: StatisticsMsg, prevState: StatisticsState): Pair<StatisticsState, StatisticsCmd?> = when (msg) {
        is LoadStatisticsSuccess -> prevState.copy(statistics = msg.statistics).noCmd()
        is LoadStatisticsFailure -> prevState.copy(statistics = Statistics.empty()).noCmd()
        is InitialIntent -> prevState withCmd StatisticsCmd.LoadStatisticsAction
        is NetworkStatus -> prevState.copy(isConnectedToInternet = msg.isConnectedToInternet).noCmd()
    }

}

// Subscription
class NetworkSubscription @Inject constructor(private val networkProvider: NetworkProvider)
    : StatelessSub<StatisticsState, StatisticsMsg>() {

    override fun invoke(): Observable<StatisticsMsg> {
        return networkProvider.isConnectedToNetworkStream().map { NetworkStatus(it) }
    }

}

//State
data class StatisticsState(val statistics: Statistics,
                           val isConnectedToInternet: Boolean) : State {
    companion object {
        fun empty() = StatisticsState(Statistics.empty(), true)
    }
}

// Msg
sealed class StatisticsMsg : Msg {
    object InitialIntent : StatisticsMsg()
    data class NetworkStatus(val isConnectedToInternet: Boolean) : StatisticsMsg()

    sealed class LoadStatisticsResult : StatisticsMsg() {
        data class LoadStatisticsSuccess(val statistics: Statistics) : LoadStatisticsResult()
        data class LoadStatisticsFailure(val error: Throwable) : LoadStatisticsResult()
    }
}

// Cmd
sealed class StatisticsCmd : Cmd {
    object LoadStatisticsAction : StatisticsCmd()
}