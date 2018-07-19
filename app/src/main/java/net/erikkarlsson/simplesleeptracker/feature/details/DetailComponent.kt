package net.erikkarlsson.simplesleeptracker.feature.details

import io.reactivex.Observable
import io.reactivex.Single
import net.erikkarlsson.simplesleeptracker.domain.entity.Sleep
import net.erikkarlsson.simplesleeptracker.elm.Cmd
import net.erikkarlsson.simplesleeptracker.elm.Component
import net.erikkarlsson.simplesleeptracker.elm.Msg
import net.erikkarlsson.simplesleeptracker.elm.State
import net.erikkarlsson.simplesleeptracker.elm.StatefulSub
import net.erikkarlsson.simplesleeptracker.elm.Sub
import net.erikkarlsson.simplesleeptracker.feature.details.domain.DeleteSleepTask
import net.erikkarlsson.simplesleeptracker.feature.details.domain.GetSleepDetailsTask
import net.erikkarlsson.simplesleeptracker.feature.details.domain.UpdateStartDateTask
import net.erikkarlsson.simplesleeptracker.feature.details.domain.UpdateTimeAsleepTask
import net.erikkarlsson.simplesleeptracker.feature.details.domain.UpdateTimeAwakeTask
import org.threeten.bp.LocalDate
import org.threeten.bp.LocalTime
import javax.inject.Inject

class DetailComponent @Inject constructor(private val sleepSubscription: SleepSubscription,
                                          private val updateStartDateTask: UpdateStartDateTask,
                                          private val updateTimeAsleepTask: UpdateTimeAsleepTask,
                                          private val updateTimeAwakeTask: UpdateTimeAwakeTask,
                                          private val deleteSleepTask: DeleteSleepTask)
    : Component<DetailState, DetailMsg, DetailCmd> {

    override fun call(cmd: DetailCmd): Single<DetailMsg> = when (cmd) {
        is UpdateStartDateCmd -> updateStartDateTask.execute(UpdateStartDateTask.Params(cmd.sleepId, cmd.startDate)).toSingleDefault(NoOp)
        is UpdateTimeAsleepCmd -> updateTimeAsleepTask.execute(UpdateTimeAsleepTask.Params(cmd.sleepId, cmd.timeAsleep)).toSingleDefault(NoOp)
        is UpdateTimeAwakeCmd -> updateTimeAwakeTask.execute(UpdateTimeAwakeTask.Params(cmd.sleepId, cmd.timeAwake)).toSingleDefault(NoOp)
        is DeleteSleepCmd -> deleteSleepTask.execute(DeleteSleepTask.Params(cmd.sleepId)).andThen(Single.just(DeleteSuccess).cast(DetailMsg::class.java))
    }

    override fun initState(): DetailState = DetailState.empty()

    override fun subscriptions(): List<Sub<DetailState, DetailMsg>> = listOf(sleepSubscription)

    override fun update(msg: DetailMsg, prevState: DetailState): Pair<DetailState, DetailCmd?> =
            when (msg) {
                NoOp -> prevState.noCmd()
                is DetailLoaded -> prevState.copy(sleep = msg.sleep).noCmd()
                is LoadDetailIntent -> prevState.copy(sleepId = msg.sleepId).noCmd()
                is PickedStartDate -> prevState withCmd UpdateStartDateCmd(prevState.sleepId, msg.startDate)
                is PickedTimeAsleep -> prevState withCmd UpdateTimeAsleepCmd(prevState.sleepId, msg.timeAsleep)
                is PickedTimeAwake -> prevState withCmd UpdateTimeAwakeCmd(prevState.sleepId, msg.timeAwake)
                DeleteClick -> prevState withCmd DeleteSleepCmd(prevState.sleepId)
                DeleteSuccess -> prevState.copy(isDeleted = true).noCmd()
            }
}

// State
data class DetailState(val sleepId: Int, val sleep: Sleep, val isDeleted: Boolean) : State {

    companion object {
        fun empty() = DetailState(0, Sleep.empty(), false)
    }
}

// Subscription
class SleepSubscription @Inject constructor(private val getSleepDetailsTask: GetSleepDetailsTask) : StatefulSub<DetailState, DetailMsg>() {
    override fun invoke(state: DetailState): Observable<DetailMsg> {
        return if (state.sleepId == 0) {
            Observable.empty()
        } else {
            getSleepDetailsTask.execute(GetSleepDetailsTask.Params(state.sleepId))
                    .map { DetailLoaded(it) }
        }
    }

    override fun isDistinct(s1: DetailState, s2: DetailState): Boolean =
            s1.sleepId != s2.sleepId
}

// Msg
sealed class DetailMsg : Msg

data class LoadDetailIntent(val sleepId: Int) : DetailMsg()
data class DetailLoaded(val sleep: Sleep) : DetailMsg()
data class PickedStartDate(val startDate: LocalDate) : DetailMsg()
data class PickedTimeAsleep(val timeAsleep: LocalTime) : DetailMsg()
data class PickedTimeAwake(val timeAwake: LocalTime) : DetailMsg()
object DeleteClick : DetailMsg()
object DeleteSuccess : DetailMsg()
object NoOp : DetailMsg()

// Cmd
sealed class DetailCmd : Cmd

data class UpdateStartDateCmd(val sleepId: Int, val startDate: LocalDate) : DetailCmd()
data class UpdateTimeAsleepCmd(val sleepId: Int, val timeAsleep: LocalTime) : DetailCmd()
data class UpdateTimeAwakeCmd(val sleepId: Int, val timeAwake: LocalTime) : DetailCmd()
data class DeleteSleepCmd(val sleepId: Int) : DetailCmd()
