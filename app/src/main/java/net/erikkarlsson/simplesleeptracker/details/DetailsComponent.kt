package net.erikkarlsson.simplesleeptracker.details

import io.reactivex.Single
import net.erikkarlsson.simplesleeptracker.domain.Sleep
import net.erikkarlsson.simplesleeptracker.elm.Cmd
import net.erikkarlsson.simplesleeptracker.elm.Component
import net.erikkarlsson.simplesleeptracker.elm.Msg
import net.erikkarlsson.simplesleeptracker.elm.State

class DetailsComponent : Component<DetailsState, Msg, DetailsCmd> {
    override fun initState(): DetailsState = DetailsState.empty()

    override fun update(msg: Msg, prevState: DetailsState): Pair<DetailsState, DetailsCmd?> = when (msg) {
        NoOp -> TODO()
        else -> prevState.noCmd()
    }

    override fun call(cmd: DetailsCmd): Single<Msg> = when (cmd) {
        else -> {
            Single.just(NoOp)
        }
    }
}

// State
data class DetailsState(val sleep: Sleep) : State {
    companion object {
        fun empty() = DetailsState(Sleep.empty())
    }
}

// Msg
object NoOp : Msg
data class LoadDetails(val id: Int): Msg
data class LoadResult(val sleep: Sleep): Msg

// Cmd
sealed class DetailsCmd : Cmd {

}

// Tasks
