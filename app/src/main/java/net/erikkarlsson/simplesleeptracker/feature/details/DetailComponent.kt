package net.erikkarlsson.simplesleeptracker.feature.details

import io.reactivex.Observable
import io.reactivex.Single
import net.erikkarlsson.simplesleeptracker.domain.SleepDataSource
import net.erikkarlsson.simplesleeptracker.domain.entity.Sleep
import net.erikkarlsson.simplesleeptracker.domain.task.UpdateStartDateTask
import net.erikkarlsson.simplesleeptracker.domain.task.UpdateTimeAsleepTask
import net.erikkarlsson.simplesleeptracker.domain.task.UpdateTimeAwakeTask
import net.erikkarlsson.simplesleeptracker.elm.*
import org.threeten.bp.LocalDate
import org.threeten.bp.LocalTime
import javax.inject.Inject

class DetailComponent @Inject constructor(private val sleepSubscription: SleepSubscription,
                                          private val updateStartDateTask: UpdateStartDateTask,
                                          private val updateTimeAsleepTask: UpdateTimeAsleepTask,
                                          private val updateTimeAwakeTask: UpdateTimeAwakeTask)
    : Component<DetailState, DetailMsg, DetailCmd> {

    override fun call(cmd: DetailCmd): Single<DetailMsg> = when (cmd) {
        is UpdateStartDateCmd -> updateStartDateTask.execute(UpdateStartDateTask.Params(cmd.sleepId, cmd.startDate)).toSingleDefault(NoOp)
        is UpdateTimeAsleepCmd -> updateTimeAsleepTask.execute(UpdateTimeAsleepTask.Params(cmd.sleepId, cmd.timeAsleep)).toSingleDefault(NoOp)
        is UpdateTimeAwakeCmd -> updateTimeAwakeTask.execute(UpdateTimeAwakeTask.Params(cmd.sleepId, cmd.timeAwake)).toSingleDefault(NoOp)
    }

    override fun initState(): DetailState = DetailState.empty()

    override fun subscriptions(): List<Sub<DetailState, DetailMsg>> = listOf(sleepSubscription)

    override fun update(msg: DetailMsg, prevState: DetailState): Pair<DetailState, DetailCmd?> = when (msg) {
        NoOp -> prevState.noCmd()
        is DetailLoaded -> prevState.copy(sleep = msg.sleep).noCmd()
        is LoadDetailIntent -> prevState.copy(sleepId = msg.sleepId).noCmd()
        is PickedStartDate -> prevState withCmd UpdateStartDateCmd(prevState.sleepId, msg.startDate)
        is PickedTimeAsleep -> prevState withCmd UpdateTimeAsleepCmd(prevState.sleepId, msg.timeAsleep)
        is PickedTimeAwake -> prevState withCmd UpdateTimeAwakeCmd(prevState.sleepId, msg.timeAwake)
    }

}

// State
data class DetailState(val sleepId: Int, val sleep: Sleep) : State {

    companion object {
        fun empty() = DetailState(0, Sleep.empty())
    }
}

// Subscription
class SleepSubscription @Inject constructor(private val sleepRepository: SleepDataSource) : StatefulSub<DetailState, DetailMsg>() {
    override fun invoke(state: DetailState): Observable<DetailMsg> {
        return if (state.sleepId == 0) {
            Observable.empty()
        } else {
            sleepRepository.getSleep(state.sleepId).map { DetailLoaded(it) }
        }
    }

    override fun isDistinct(s1: DetailState, s2: DetailState): Boolean =
            s1.sleep.id == s2.sleep.id
}

// Msg
sealed class DetailMsg : Msg

data class LoadDetailIntent(val sleepId: Int) : DetailMsg()
data class DetailLoaded(val sleep: Sleep) : DetailMsg()
data class PickedStartDate(val startDate: LocalDate) : DetailMsg()
data class PickedTimeAsleep(val timeAsleep: LocalTime) : DetailMsg()
data class PickedTimeAwake(val timeAwake: LocalTime) : DetailMsg()
object NoOp : DetailMsg()

// Cmd
sealed class DetailCmd : Cmd

data class UpdateStartDateCmd(val sleepId: Int, val startDate: LocalDate) : DetailCmd()
data class UpdateTimeAsleepCmd(val sleepId: Int, val timeAsleep: LocalTime) : DetailCmd()
data class UpdateTimeAwakeCmd(val sleepId: Int, val timeAwake: LocalTime) : DetailCmd()
