package net.erikkarlsson.simplesleeptracker.feature.add

import io.reactivex.Single
import net.erikkarlsson.simplesleeptracker.domain.DateTimeProvider
import net.erikkarlsson.simplesleeptracker.domain.entity.Sleep
import net.erikkarlsson.simplesleeptracker.elm.Cmd
import net.erikkarlsson.simplesleeptracker.elm.Component
import net.erikkarlsson.simplesleeptracker.elm.Msg
import net.erikkarlsson.simplesleeptracker.elm.State
import net.erikkarlsson.simplesleeptracker.feature.add.domain.AddSleepTask
import org.threeten.bp.LocalDate
import org.threeten.bp.LocalTime
import org.threeten.bp.ZoneOffset
import javax.inject.Inject

class AddSleepComponent @Inject constructor(private val addSleepTask: AddSleepTask,
                                            private val dateTimeProvider: DateTimeProvider)
    : Component<AddSleepState, AddSleepMsg, AddSleepCmd> {

    override fun call(sleepCmd: AddSleepCmd): Single<AddSleepMsg> =
            when (sleepCmd) {
                is SaveSleepCmd -> onAddSleepCmd(sleepCmd.addSleepState.sleep)
            }

    override fun initState(): AddSleepState {
        val startDate = dateTimeProvider.now().minusDays(1).toLocalDate()
        val startTime = LocalTime.of(DEFAULT_START_TIME_HOUR, DEFAULT_START_TIME_MINUTE)
        val endTime = LocalTime.of(DEFAULT_END_TIME_HOUR, DEFAULT_END_TIME_MINUTE)
        val zoneOffset = dateTimeProvider.now().getOffset()
        return AddSleepState(startDate, startTime, endTime, zoneOffset, false)
    }

    override fun update(msg: AddSleepMsg, prevState: AddSleepState): Pair<AddSleepState, AddSleepCmd?> =
            when (msg) {
                NoOp -> prevState.noCmd()
                is PickedTimeAsleep -> prevState.copy(startTime = msg.timeAsleep).noCmd()
                is PickedStartDate -> prevState.copy(startDate = msg.startDate).noCmd()
                is PickedTimeAwake -> prevState.copy(endTime = msg.timeAwake).noCmd()
                SaveClick -> prevState withCmd SaveSleepCmd(prevState)
                SaveSuccess -> prevState.copy(isSaveSuccess = true).noCmd()
            }

    private fun onAddSleepCmd(sleep: Sleep): Single<AddSleepMsg> =
            addSleepTask.execute(AddSleepTask.Params(sleep))
                    .andThen(Single.just(SaveSuccess).cast(AddSleepMsg::class.java))
}

// State
data class AddSleepState(val startDate: LocalDate,
                         val startTime: LocalTime,
                         val endTime: LocalTime,
                         val zoneOffset: ZoneOffset,
                         val isSaveSuccess: Boolean) : State {

    val hoursSlept: Float
        get() = sleep.hours

    val sleep: Sleep
        get() = Sleep.from(startDate = startDate,
                           startTime = startTime,
                           endTime = endTime,
                           zoneOffset = zoneOffset)

    companion object {
        fun empty() = AddSleepState(LocalDate.MIN, LocalTime.MIN, LocalTime.MIN, ZoneOffset.MIN, false)
    }
}

// Msg
sealed class AddSleepMsg : Msg

data class PickedStartDate(val startDate: LocalDate) : AddSleepMsg()
data class PickedTimeAsleep(val timeAsleep: LocalTime) : AddSleepMsg()
data class PickedTimeAwake(val timeAwake: LocalTime) : AddSleepMsg()
object SaveClick : AddSleepMsg()
object SaveSuccess : AddSleepMsg()
object NoOp : AddSleepMsg()

// Cmd
sealed class AddSleepCmd : Cmd

data class SaveSleepCmd(val addSleepState: AddSleepState) : AddSleepCmd()
