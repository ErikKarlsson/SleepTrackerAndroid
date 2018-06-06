package net.erikkarlsson.simplesleeptracker.feature.main

import io.reactivex.Single
import net.erikkarlsson.simplesleeptracker.domain.task.CompletableTask.None
import net.erikkarlsson.simplesleeptracker.domain.task.RestoreSleepBackupTask
import net.erikkarlsson.simplesleeptracker.elm.Cmd
import net.erikkarlsson.simplesleeptracker.elm.Component
import net.erikkarlsson.simplesleeptracker.elm.Msg
import net.erikkarlsson.simplesleeptracker.elm.State
import net.erikkarlsson.simplesleeptracker.elm.Sub
import javax.inject.Inject

class MainComponent @Inject constructor(private val restoreSleepBackupTask: RestoreSleepBackupTask)
    : Component<MainState, MainMsg, MainCmd> {

    override fun call(cmd: MainCmd): Single<MainMsg> = when(cmd) {
        RestoreBackup -> restoreSleepBackupTask.execute(None()).toSingleDefault(NoOp)
    }

    override fun initState(): MainState = MainState.empty()

    override fun subscriptions(): List<Sub<MainState, MainMsg>> = listOf()

    override fun update(msg: MainMsg, prevState: MainState): Pair<MainState, MainCmd?> = when (msg) {
        NoOp -> prevState.noCmd()
        SignInSuccess -> prevState.copy(isLoggedIn = true) withCmd RestoreBackup
        SignInCancelled -> prevState.copy(isLoggedIn = false).noCmd()
        SignInFailed -> prevState.copy(isLoggedIn = false).noCmd()
    }

}

// State
data class MainState(val isLoggedIn: Boolean) : State {

    companion object {
        fun empty() = MainState(false)
    }
}

// Msg
sealed class MainMsg : Msg

object SignInSuccess: MainMsg()
object SignInCancelled: MainMsg()
object SignInFailed: MainMsg()
object NoOp : MainMsg()

// Cmd
sealed class MainCmd : Cmd
object RestoreBackup: MainCmd()
