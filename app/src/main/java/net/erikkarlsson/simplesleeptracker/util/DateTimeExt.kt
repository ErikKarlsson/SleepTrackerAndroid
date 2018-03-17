package net.erikkarlsson.simplesleeptracker.util

import net.erikkarlsson.simplesleeptracker.domain.DayOfWeekLocalTime
import org.threeten.bp.DayOfWeek
import org.threeten.bp.Duration
import org.threeten.bp.LocalTime
import org.threeten.bp.OffsetDateTime
import org.threeten.bp.format.DateTimeFormatter.ofPattern
import org.threeten.bp.format.TextStyle
import org.threeten.bp.temporal.ChronoUnit
import java.math.BigDecimal
import java.util.*

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

val OffsetDateTime.formatYYYYMMDD: String get() = this.format(ofPattern("yyyy-MM-dd"))
val OffsetDateTime.formatYYYYMMDDHHMM: String get() = this.format(ofPattern("yyyy-MM-dd HH:mm"))

val LocalTime.formatHHMM: String get() = this.format(ofPattern("HH:mm"))

val Float.formatHHMM: String
    get() {
        val hours = Math.floor(this.toDouble()).toInt()
        val minutes = Math.floor(((this - hours) * 60).toDouble()).toInt()
        return String.format("%dh %dmin", hours, minutes)
    }

fun Float.formatDiffPercentage(other: Float): String {
    val diff = this - other
    val prefix = if (diff > 0) "+" else if (diff < 0) "-" else ""
    return String.format("%s%d%%", prefix, Math.round((diff) / other * 100))
}

fun LocalTime.formatDiffHHMM(other: LocalTime): String {
    val diffHours = Duration.between(other, this).toMinutes().toFloat() / 60f
    val prefix = if (diffHours > 0) "+" else if (diffHours < 0) "-" else ""
    val diffHHMM = if (Math.abs(diffHours) > 1f) diffHours.formatHHMM else diffHours.formatMM
    return prefix + diffHHMM
}

fun Float.formatDiffHHMM(other: Float): String {
    val diff = this - other
    val prefix = if (diff > 0) "+" else if (diff < 0) "-" else ""
    val diffHHMM = if (Math.abs(diff) > 1f) diff.formatHHMM else diff.formatMM
    return prefix + diffHHMM
}

val Float.formatMM: String
    get() {
        return String.format("%dmin", Math.floor((this * 60).toDouble()).toInt())
    }

fun Int.formatPercentageDiff(other: Int): String {
    val diff = other - this
    val prefix = if (diff > 0) "+" else if (diff < 0) "-" else ""
    return String.format("%s%s%%", prefix, diff)
}

val Int.toLocalTime: LocalTime get() = LocalTime.of(0, 0).plusSeconds(this.toLong())

val DayOfWeekLocalTime.formatDisplayName: String
    get() = String.format("%s: %s",
            DayOfWeek.of(this.dayOfWeek).getDisplayName(TextStyle.FULL,
                    Locale.getDefault()).capitalize(),
            this.localTime.formatHHMM)
