package net.erikkarlsson.simplesleeptracker.util

import net.erikkarlsson.simplesleeptracker.domain.*
import org.threeten.bp.Duration
import org.threeten.bp.LocalTime
import org.threeten.bp.OffsetDateTime
import org.threeten.bp.temporal.ChronoUnit
import java.math.BigDecimal

/**
 * Calculates seconds from midnight.
 * For time between 00:00 - 12:00 the offset will be positive. e.g. 60 for 00:01
 * For time between 12:00 - 00:00 the offset will be negative. e.g. -60 for 23:59
 * The result can be used to calculate average time for a set of times.
 */
val OffsetDateTime.midnightOffsetInSeconds: Int
    get() {
        val seconds = this.hour.hoursToSeconds + this.minute.minutesToSeconds + this.second
        return if (this.hour <= TWELVE_IN_THE_AFTERNOON) seconds else seconds - SECONDS_IN_A_DAY
    }

val LocalTime.midnightOffsetInSeconds: Int
    get() {
        val seconds = this.hour.hoursToSeconds + this.minute.minutesToSeconds + this.second
        return if (this.hour <= TWELVE_IN_THE_AFTERNOON) seconds else seconds - SECONDS_IN_A_DAY
    }

/**
 * Calculates amount of hours between two dates. e.g. 7.850 hours
 */
fun OffsetDateTime.hoursTo(dateTime: OffsetDateTime): Float {
    val from = this.withSecond(0) // Remove seconds to fix rounding errors.
    val to = dateTime.withSecond(0)
    val secondsBetweenDates = ChronoUnit.SECONDS.between(from, to)
    return BigDecimal.valueOf(secondsBetweenDates / SECONDS_IN_AN_HOUR.toDouble())
            .setScale(HOURS_PRECISION, BigDecimal.ROUND_HALF_UP)
            .toFloat()
}

/**
 * Amount of hours between two times.
 * e.g. 22:30 to 06:30 eq 8.0 hours
 * e.g. 22:30 to 21:30 eq -1 hours
 */
fun LocalTime.hoursTo(other: LocalTime): Float = Duration.between(this, other).toMinutes().toFloat() / MINUTES_IN_AN_HOUR

/**
 * Converts midnight offset in seconds to LocalTime.
 */
val Int.midnightOffsetToLocalTime: LocalTime get() = LocalTime.of(0, 0).plusSeconds(this.toLong())

/**
 * Converts a string containing ISO date into OffsetDateTime. e.g. "2018-03-07T21:30:00+01:00"
 */
val String.offsetDateTime: OffsetDateTime get() = OffsetDateTime.parse(this)

/**
 * Time unit conversion
 */
val Int.hoursToSeconds: Int get() = this * SECONDS_IN_AN_HOUR
val Int.minutesToSeconds: Int get() = this * SECONDS_IN_A_MINUTE
