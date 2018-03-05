package net.erikkarlsson.simplesleeptracker.sleepappwidget

import io.reactivex.Single
import net.erikkarlsson.simplesleeptracker.base.LoadCurrentSleepTask
import net.erikkarlsson.simplesleeptracker.base.ToggleSleepTask
import net.erikkarlsson.simplesleeptracker.domain.Sleep
import net.erikkarlsson.simplesleeptracker.elm.Cmd
import net.erikkarlsson.simplesleeptracker.elm.Component
import net.erikkarlsson.simplesleeptracker.elm.Msg
import net.erikkarlsson.simplesleeptracker.elm.State
import net.erikkarlsson.simplesleeptracker.sleepappwidget.WidgetMsg.*
import javax.inject.Inject

class AppWidgetComponent @Inject constructor(private val loadCurrentSleep: LoadCurrentSleep,
                                             private val toggleSleep: ToggleSleep)
    : Component<WidgetState, WidgetMsg, WidgetCmd> {

    override fun call(cmd: WidgetCmd): Single<WidgetMsg> = when (cmd) {
        WidgetCmd.LoadCurrentSleepAction -> loadCurrentSleep.task()
        WidgetCmd.ToggleSleepAction -> toggleSleep.task()
    }

    override fun initState(): WidgetState = WidgetState.empty()

    override fun update(msg: WidgetMsg, prevState: WidgetState): Pair<WidgetState, WidgetCmd?> = when (msg) {
        UpdateMsg -> prevState.withCmd(WidgetCmd.LoadCurrentSleepAction)
        ToggleSleepMsg -> prevState.withCmd(WidgetCmd.ToggleSleepAction)
        is LoadCurrentSleepResult.Success -> prevState.copy(isSleeping = msg.sleep.isSleeping).noCmd()
        is LoadCurrentSleepResult.Failure -> prevState.copy(isSleeping = false).noCmd()
        is ToggleSleepResult.Success -> prevState.copy(isSleeping = msg.sleep.isSleeping).noCmd()
        is ToggleSleepResult.Failure -> prevState.copy(isSleeping = false).noCmd()
    }

}

// State
data class WidgetState(val isSleeping: Boolean) : State {
    companion object {
        fun empty() = WidgetState(false)
    }
}

// Msg
sealed class WidgetMsg : Msg {
    object UpdateMsg : WidgetMsg()
    object ToggleSleepMsg : WidgetMsg()

    sealed class LoadCurrentSleepResult() : WidgetMsg() {
        data class Success(val sleep: Sleep) : LoadCurrentSleepResult()
        data class Failure(val error: Throwable) : LoadCurrentSleepResult()
    }

    sealed class ToggleSleepResult() : WidgetMsg() {
        data class Success(val sleep: Sleep) : ToggleSleepResult()
        data class Failure(val error: Throwable) : ToggleSleepResult()
    }
}

// Cmd
sealed class WidgetCmd : Cmd {
    object LoadCurrentSleepAction : WidgetCmd()
    object ToggleSleepAction : WidgetCmd()
}

// Tasks
class LoadCurrentSleep @Inject constructor(private val loadCurrentSleepTask: LoadCurrentSleepTask) {

    internal fun task(): Single<WidgetMsg> = loadCurrentSleepTask.execute()
        .map { WidgetMsg.LoadCurrentSleepResult.Success(it) }
        .cast(WidgetMsg::class.java)
        .onErrorReturn(WidgetMsg.LoadCurrentSleepResult::Failure)
}

class ToggleSleep @Inject constructor(private val toggleSleepTask: ToggleSleepTask) {

    internal fun task() = toggleSleepTask.execute()
        .map { WidgetMsg.ToggleSleepResult.Success(it) }
        .cast(WidgetMsg::class.java)
        .onErrorReturn { WidgetMsg.ToggleSleepResult.Failure(it) }
}