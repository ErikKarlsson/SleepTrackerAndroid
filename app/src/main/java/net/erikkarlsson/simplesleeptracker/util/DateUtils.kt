package net.erikkarlsson.simplesleeptracker.util

import org.threeten.bp.OffsetDateTime
import org.threeten.bp.temporal.ChronoUnit
import java.math.BigDecimal

object DateTimeUtils {

    /**
     * Calculates amount of hours with 3 decimal precision between two dates. eg. 7.850
     */
    fun calculateHoursBetweenDates(fromDate: OffsetDateTime, toDate: OffsetDateTime): Float {
        val secondsBetweenDates = ChronoUnit.SECONDS.between(fromDate, toDate)
        return BigDecimal.valueOf(secondsBetweenDates / 3600.0)
                .setScale(3, BigDecimal.ROUND_HALF_UP)
                .toFloat()
    }

    /**
     * Calculates offset from midnight in minutes.
     * For time between 00:00 - 12:00 the offset will be positive. eg. 60 minutes for 01:00
     * For time between 12:00 - 00:00 the offset will be negative. eg. -60 minutes for 23:00
     * The offset can be used to calculate average time.
     */
    fun calculateOffsetFromMidnightInSeconds(date: OffsetDateTime): Int {
        val seconds = date.hour * 3600 + date.minute * 60 + date.second
        val secondsInADay = 86400

        if (date.hour <= 12) {
            return seconds
        } else {
            return seconds - secondsInADay
        }
    }

}