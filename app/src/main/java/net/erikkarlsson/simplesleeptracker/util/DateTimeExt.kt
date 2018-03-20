package net.erikkarlsson.simplesleeptracker.util

import net.erikkarlsson.simplesleeptracker.base.HOURS_PRECISION
import org.threeten.bp.Duration
import org.threeten.bp.LocalTime
import org.threeten.bp.OffsetDateTime
import org.threeten.bp.temporal.ChronoUnit
import java.math.BigDecimal

/**
 * Calculates seconds from midnight.
 * For time between 00:00 - 12:00 the offset will be positive. eg. 60 seconds for 00:01
 * For time between 12:00 - 00:00 the offset will be negative. eg. -60 seconds for 23:59
 * The result can be used to calculate an average time for a set of times.
 */
val OffsetDateTime.midnightOffsetInSeconds: Int
    get() {
        val seconds = this.hour * 3600 + this.minute * 60 + this.second
        val secondsInADay = 86400
        return if (this.hour <= 12) seconds else seconds - secondsInADay
    }

/**
 * Calculates amount of hours with 3 decimal precision between two dates. eg. 7.850
 */
fun OffsetDateTime.hoursTo(dateTime: OffsetDateTime): Float {
    val secondsBetweenDates = ChronoUnit.SECONDS.between(this, dateTime)
    return BigDecimal.valueOf(secondsBetweenDates / 3600.0)
        .setScale(HOURS_PRECISION, BigDecimal.ROUND_HALF_UP)
        .toFloat()
}

/**
 * Amount of hours between two points in time.
 */
fun LocalTime.diffHours(other: LocalTime): Float = Duration.between(other, this).toMinutes().toFloat() / 60f

/**
 * Converts midnight offset in seconds to LocalTime.
 */
val Int.localTime: LocalTime get() = LocalTime.of(0, 0).plusSeconds(this.toLong())

/**
 * Converts a string containing a ISO date into OffsetDateTime. eg. "2018-03-07T21:30:00+01:00"
 */
val String.offsetDateTime: OffsetDateTime get() = OffsetDateTime.parse(this)