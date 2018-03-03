package net.erikkarlsson.simplesleeptracker.statisticselm

import io.reactivex.Single
import net.erikkarlsson.simplesleeptracker.domain.Statistics
import net.erikkarlsson.simplesleeptracker.elm.Cmd
import net.erikkarlsson.simplesleeptracker.elm.Component
import net.erikkarlsson.simplesleeptracker.elm.Msg
import net.erikkarlsson.simplesleeptracker.elm.State
import net.erikkarlsson.simplesleeptracker.statisticselm.StatisticsMsg.LoadStatisticsResult.LoadStatisticsFailure
import net.erikkarlsson.simplesleeptracker.statisticselm.StatisticsMsg.LoadStatisticsResult.LoadStatisticsSuccess
import javax.inject.Inject

class StatisticsComponent @Inject constructor(private val loadStatistics: net.erikkarlsson.simplesleeptracker.statisticselm.task.LoadStatistics)
    : Component<StatisticsState, StatisticsMsg, StatisticsCmd> {

    override fun call(cmd: StatisticsCmd): Single<StatisticsMsg> = when (cmd) {
        is StatisticsCmd.LoadStatisticsAction -> loadStatistics.task()
    }

    override fun initState(): StatisticsState = StatisticsState.empty()

    override fun update(msg: StatisticsMsg, prevState: StatisticsState): Pair<StatisticsState, StatisticsCmd?> = when (msg) {
        is LoadStatisticsSuccess -> prevState.copy(msg.statistics).noCmd()
        is LoadStatisticsFailure -> prevState.copy(Statistics.empty()).noCmd()
        StatisticsMsg.InitialIntent -> prevState withCmd StatisticsCmd.LoadStatisticsAction
    }

}

//State
data class StatisticsState(val statistics: Statistics) : State {
    companion object {
        fun empty() = StatisticsState(Statistics.empty())
    }
}

// Msg
sealed class StatisticsMsg : Msg {
    object InitialIntent : StatisticsMsg()

    sealed class LoadStatisticsResult : StatisticsMsg() {
        data class LoadStatisticsSuccess(val statistics: Statistics) : LoadStatisticsResult()
        data class LoadStatisticsFailure(val error: Throwable) : LoadStatisticsResult()
    }
}

// Cmd
sealed class StatisticsCmd : Cmd {
    object LoadStatisticsAction : StatisticsCmd()
}