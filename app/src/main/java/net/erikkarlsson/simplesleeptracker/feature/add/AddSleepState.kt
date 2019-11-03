package net.erikkarlsson.simplesleeptracker.feature.add

import com.airbnb.mvrx.MvRxState
import net.erikkarlsson.simplesleeptracker.domain.entity.Sleep
import org.threeten.bp.LocalDate
import org.threeten.bp.LocalTime
import org.threeten.bp.ZoneOffset

data class AddSleepState(val startDate: LocalDate = LocalDate.MIN,
                         val startTime: LocalTime = LocalTime.MIN,
                         val endTime: LocalTime = LocalTime.MIN,
                         val zoneOffset: ZoneOffset = ZoneOffset.MIN,
                         val isSaveSuccess: Boolean = false) : MvRxState {

    val hoursSlept: Float
        get() = sleep.hours

    val sleep: Sleep
        get() = Sleep.from(startDate = startDate,
                startTime = startTime,
                endTime = endTime,
                zoneOffset = zoneOffset)

    companion object {
        fun empty() = AddSleepState()
    }
}

