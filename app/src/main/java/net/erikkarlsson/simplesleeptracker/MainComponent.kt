package net.erikkarlsson.simplesleeptracker

import io.reactivex.Single
import net.erikkarlsson.simplesleeptracker.elm.Cmd
import net.erikkarlsson.simplesleeptracker.elm.Component
import net.erikkarlsson.simplesleeptracker.elm.Msg
import net.erikkarlsson.simplesleeptracker.elm.State
import net.erikkarlsson.simplesleeptracker.elm.Sub
import javax.inject.Inject

class MainComponent
@Inject constructor()
    : Component<MainState, MainMsg, MainCmd> {

    override fun call(cmd: MainCmd): Single<MainMsg> {
        return Single.just(NoOp)
    }

    override fun initState(): MainState = MainState.empty()

    override fun subscriptions(): List<Sub<MainState, MainMsg>> = listOf()

    override fun update(msg: MainMsg, prevState: MainState): Pair<MainState, MainCmd?> = when (msg) {
        NoOp -> prevState.noCmd()
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

object NoOp : MainMsg()

// Cmd
sealed class MainCmd : Cmd
