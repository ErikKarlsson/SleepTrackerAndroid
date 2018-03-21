package net.erikkarlsson.simplesleeptracker.util

import net.erikkarlsson.simplesleeptracker.domain.entity.DayOfWeekLocalTime
import org.threeten.bp.LocalTime
import org.threeten.bp.OffsetDateTime
import org.threeten.bp.format.DateTimeFormatter
import org.threeten.bp.format.TextStyle
import java.util.*

val OffsetDateTime.formatHHMM: String get() = this.format(DateTimeFormatter.ofPattern("HH:mm"))
val OffsetDateTime.formatDateDisplayName: String get() = this.format(DateTimeFormatter.ofPattern("EEE, MMM d"))

val DayOfWeekLocalTime.formatDisplayName: String
    get() = String.format("%s: %s",
            this.dayOfWeek.getDisplayName(TextStyle.FULL, Locale.getDefault()).capitalize(),
            this.localTime.formatHHMM)

val List<DayOfWeekLocalTime>.formatDisplayName: String
    get() = this.map { " - " + it.formatDisplayName }.joinToString(separator = "\n")

val LocalTime.formatHHMM: String get() = this.format(DateTimeFormatter.ofPattern("HH:mm"))

val Int.formatPercentage: String  get() {
    val prefix = if (this > 0) "+" else if (this < 0) "-" else ""
    return String.format("%s%d%%", prefix, this)
}

val Float.formatHoursMinutes: String
    get() {
        val hours: Int = Math.floor(Math.abs(this).toDouble()).toInt()
        val minutes: Int = Math.floor(((Math.abs(this) - hours) * 60).toDouble()).toInt()
        return when {
            hours == 0 && minutes > 0 -> String.format("%dmin", minutes)
            hours > 0 && minutes == 0 -> String.format("%dh", hours)
            hours > 0 && minutes > 0 -> String.format("%dh %dmin", hours, minutes)
            else -> "0h"
        }
    }

val Float.formatHoursMinutesWithPrefix: String
    get() {
        val prefix = if (this > 0) "+" else "-"
        return prefix + this.formatHoursMinutes
    }

