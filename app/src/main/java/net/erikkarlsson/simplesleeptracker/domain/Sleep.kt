package net.erikkarlsson.simplesleeptracker.domain

import net.erikkarlsson.simplesleeptracker.util.hoursTo
import net.erikkarlsson.simplesleeptracker.util.offsetFromMidnightInSeconds
import org.threeten.bp.OffsetDateTime

data class Sleep(val id: Int = 0,
                 val fromDate: OffsetDateTime,
                 val toDate: OffsetDateTime? = null) {

    val fromDateMidnightOffsetSeconds: Int
        get() = fromDate.offsetFromMidnightInSeconds

    val toDateMidnightOffsetSeconds: Int
        get() = toDate?.offsetFromMidnightInSeconds ?: 0

    val hours: Float
        get() = toDate?.let { fromDate.hoursTo(toDate) } ?: 0.0f

    companion object {
        fun empty() = Sleep(-1, OffsetDateTime.now(), null)
    }

}