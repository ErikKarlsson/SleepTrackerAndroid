package net.erikkarlsson.simplesleeptracker.domain.entity

import net.erikkarlsson.simplesleeptracker.dateutil.hoursTo
import net.erikkarlsson.simplesleeptracker.dateutil.midnightOffsetInSeconds
import net.erikkarlsson.simplesleeptracker.dateutil.offsetDateTime
import org.threeten.bp.LocalDate
import org.threeten.bp.LocalTime
import org.threeten.bp.OffsetDateTime
import org.threeten.bp.ZoneOffset
import org.threeten.bp.temporal.ChronoUnit

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

        /**
         * Create Sleep session of maximum 24 hours.
         */
        fun from(id: Int? = null,
                 startDate: LocalDate,
                 startTime: LocalTime,
                 endTime: LocalTime,
                 zoneOffset: ZoneOffset): Sleep {
            val fromDate = OffsetDateTime.of(startDate, startTime, zoneOffset)

            val toDate = if (startTime < endTime) {
                fromDate.with(endTime) // User woke up the same day.
            } else {
                fromDate.with(endTime).plusDays(1) // User woke up the following day.
            }

            return Sleep(id = id, fromDate = fromDate, toDate = toDate)
        }

        fun from(fromDate: String, toDate: String) =
                Sleep(fromDate = fromDate.offsetDateTime, toDate = toDate.offsetDateTime)
    }

}

fun Sleep.shiftStartDate(startDate: LocalDate): Sleep {
    val days = ChronoUnit.DAYS.between(this.fromDate.toLocalDate(), startDate)
    val fromDate = this.fromDate.plusDays(days)
    val toDate = this.toDate?.plusDays(days)
    return this.copy(fromDate = fromDate, toDate = toDate)
}
