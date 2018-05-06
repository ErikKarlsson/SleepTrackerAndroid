package net.erikkarlsson.simplesleeptracker.feature.appwidget

import io.reactivex.Observable
import io.reactivex.Single
import net.erikkarlsson.simplesleeptracker.domain.SleepDataSource
import net.erikkarlsson.simplesleeptracker.domain.entity.Sleep
import net.erikkarlsson.simplesleeptracker.domain.task.ToggleSleepTask
import net.erikkarlsson.simplesleeptracker.elm.*
import net.erikkarlsson.simplesleeptracker.feature.appwidget.WidgetCmd.ToggleSleepCmd
import javax.inject.Inject

class AppWidgetComponent @Inject constructor(private val toggleSleepTask: ToggleSleepTask,
                                             private val sleepSubscription: SleepSubscription)
    : Component<WidgetState, WidgetMsg, WidgetCmd> {

    override fun call(cmd: WidgetCmd): Single<WidgetMsg> = when (cmd) {
        ToggleSleepCmd -> toggleSleepTask.execute().toSingleDefault(NoOp)
    }

    override fun subscriptions(): List<Sub<WidgetState, WidgetMsg>> = listOf(sleepSubscription)

    override fun initState(): WidgetState = WidgetState.empty()

    override fun update(msg: WidgetMsg, prevState: WidgetState): Pair<WidgetState, WidgetCmd?> = when (msg) {
        ToggleSleepClicked -> prevState.withCmd(ToggleSleepCmd)
        is CurrentSleepLoaded -> prevState.copy(isSleeping = msg.sleep.isSleeping).noCmd()
        WidgetOnUpdate -> prevState.copy(updateCount = (prevState.updateCount + 1)).noCmd() // Increase counter to re-render app widget on update
        NoOp -> prevState.noCmd()
    }

}

// State
data class WidgetState(val isSleeping: Boolean, val updateCount: Int) : State {
    companion object {
        fun empty() = WidgetState(false, 0)
    }
}

// Subscription
class SleepSubscription @Inject constructor(private val sleepRepository: SleepDataSource) : StatelessSub<WidgetState, WidgetMsg>() {

    override fun invoke(): Observable<WidgetMsg> = sleepRepository.getCurrent().map { CurrentSleepLoaded(it) }
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