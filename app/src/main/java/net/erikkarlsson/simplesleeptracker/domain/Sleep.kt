package net.erikkarlsson.simplesleeptracker.domain

import net.erikkarlsson.simplesleeptracker.util.hoursTo
import net.erikkarlsson.simplesleeptracker.util.midnightOffsetInSeconds
import org.threeten.bp.OffsetDateTime

data class Sleep(val id: Int? = null,
                 val fromDate: OffsetDateTime,
                 val toDate: OffsetDateTime? = null) {

    val fromDateMidnightOffsetSeconds: Int
        get() = fromDate.midnightOffsetInSeconds

    val toDateMidnightOffsetSeconds: Int
        get() = toDate?.midnightOffsetInSeconds ?: 0

    val hours: Float
        get() = toDate?.let { fromDate.hoursTo(toDate) } ?: 0.0f

    val isSleeping: Boolean
        get() = this != empty() && toDate == null

    companion object {
        fun empty() = Sleep(null, OffsetDateTime.MIN, null)
    }

}