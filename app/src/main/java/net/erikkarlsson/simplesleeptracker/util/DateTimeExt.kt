package net.erikkarlsson.simplesleeptracker.util

import net.erikkarlsson.simplesleeptracker.base.HOURS_PRECISION
import net.erikkarlsson.simplesleeptracker.base.MINUTES_IN_AN_HOUR
import net.erikkarlsson.simplesleeptracker.base.SECONDS_IN_AN_HOUR
import net.erikkarlsson.simplesleeptracker.base.SECONDS_IN_A_DAY
import net.erikkarlsson.simplesleeptracker.base.SECONDS_IN_A_MINUTE
import net.erikkarlsson.simplesleeptracker.base.TWELVE_IN_THE_AFTERNOON
import org.threeten.bp.Duration
import org.threeten.bp.LocalTime
import org.threeten.bp.OffsetDateTime
import org.threeten.bp.temporal.ChronoUnit
import java.math.BigDecimal
import java.text.SimpleDateFormat
import java.util.*

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

/**
 * Calculates amount of hours between two dates. e.g. 7.850
 */
fun OffsetDateTime.hoursBetween(dateTime: OffsetDateTime): Float {
    val secondsBetweenDates = ChronoUnit.SECONDS.between(this, dateTime)
    return BigDecimal.valueOf(secondsBetweenDates / SECONDS_IN_AN_HOUR.toDouble())
        .setScale(HOURS_PRECISION, BigDecimal.ROUND_HALF_UP)
        .toFloat()
}

/**
 * Amount of hours between two points in time. e.g. 7.850
 */
fun LocalTime.diffHours(other: LocalTime): Float = Duration.between(this, other).toMinutes().toFloat() / MINUTES_IN_AN_HOUR

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

val Long.formatTimestamp: String get() {
    val date = Date(this)
    val format = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
    return format.format(date)
}