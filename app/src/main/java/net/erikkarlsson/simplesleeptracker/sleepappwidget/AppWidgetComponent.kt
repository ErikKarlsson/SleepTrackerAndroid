package net.erikkarlsson.simplesleeptracker.sleepappwidget

import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import net.erikkarlsson.simplesleeptracker.base.ToggleSleepTask
import net.erikkarlsson.simplesleeptracker.domain.Sleep
import net.erikkarlsson.simplesleeptracker.domain.SleepDataSource
import net.erikkarlsson.simplesleeptracker.elm.*
import javax.inject.Inject

class AppWidgetComponent @Inject constructor(private val toggleSleepTask: ToggleSleepTask,
                                             private val sleepSubscription: SleepSubscription)
    : Component<WidgetState, WidgetMsg, WidgetCmd> {

    override fun call(cmd: WidgetCmd): Single<WidgetMsg> = when (cmd) {
        WidgetCmd.ToggleSleepCmd -> toggleSleepTask.execute().toSingleDefault(NoOp)
    }

    override fun subscriptions(): List<Sub<WidgetState, WidgetMsg>> = listOf(sleepSubscription)

    override fun initState(): WidgetState = WidgetState.empty()

    override fun update(msg: WidgetMsg, prevState: WidgetState): Pair<WidgetState, WidgetCmd?> = when (msg) {
        ToggleSleepClicked -> prevState.withCmd(WidgetCmd.ToggleSleepCmd)
        is CurrentSleepLoaded -> prevState.copy(isSleeping = msg.sleep.isSleeping).noCmd()
        WidgetOnUpdate -> prevState.copy(updateCounter = (prevState.updateCounter + 1)).noCmd() // Increase counter to re-render widget on update
        NoOp -> prevState.noCmd()
    }

}

// State
data class WidgetState(val isSleeping: Boolean, val updateCounter: Int) : State {
    companion object {
        fun empty() = WidgetState(false, 0)
    }
}

// Subscription
class SleepSubscription @Inject constructor(private val sleepRepository: SleepDataSource)
    : StatelessSub<WidgetState, WidgetMsg>() {

    override fun invoke(): Observable<WidgetMsg> {
        return sleepRepository.getCurrent()
            .subscribeOn(Schedulers.io())
            .map { CurrentSleepLoaded(it) }
    }

}

// Msg
sealed class WidgetMsg : Msg

object ToggleSleepClicked : WidgetMsg()
object WidgetOnUpdate : WidgetMsg()
data class CurrentSleepLoaded(val sleep: Sleep) : WidgetMsg()
object NoOp : WidgetMsg()

// Cmd
sealed class WidgetCmd : Cmd {
    object ToggleSleepCmd : WidgetCmd()
}