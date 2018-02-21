package net.erikkarlsson.simplesleeptracker.util

import org.threeten.bp.OffsetDateTime
import org.threeten.bp.temporal.ChronoUnit
import java.math.BigDecimal

/**
 * Calculates offset from midnight in minutes.
 * For time between 00:00 - 12:00 the offset will be positive. eg. 60 minutes for 01:00
 * For time between 12:00 - 00:00 the offset will be negative. eg. -60 minutes for 23:00
 * The offset can be used to calculate average time.
 */
val OffsetDateTime.offsetFromMidnightInSeconds: Int
    get() {
        val seconds = this.hour * 3600 + this.minute * 60 + this.second
        val secondsInADay = 86400

        if (this.hour <= 12) {
            return seconds
        } else {
            return seconds - secondsInADay
        }
    }

/**
 * Calculates amount of hours with 3 decimal precision between two dates. eg. 7.850
 */
fun OffsetDateTime.hoursTo(dateTime: OffsetDateTime): Float {
    val secondsBetweenDates = ChronoUnit.SECONDS.between(this, dateTime)
    return BigDecimal.valueOf(secondsBetweenDates / 3600.0)
        .setScale(3, BigDecimal.ROUND_HALF_UP)
        .toFloat()
}