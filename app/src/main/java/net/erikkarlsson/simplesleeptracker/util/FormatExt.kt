package net.erikkarlsson.simplesleeptracker.util

import com.google.common.collect.ImmutableList
import net.erikkarlsson.simplesleeptracker.base.MINUTES_IN_AN_HOUR
import net.erikkarlsson.simplesleeptracker.data.DayOfWeekHours
import net.erikkarlsson.simplesleeptracker.domain.entity.DayOfWeekLocalTime
import org.threeten.bp.LocalDate
import org.threeten.bp.LocalTime
import org.threeten.bp.OffsetDateTime
import org.threeten.bp.format.DateTimeFormatter
import org.threeten.bp.format.TextStyle
import java.text.SimpleDateFormat
import java.util.*

val OffsetDateTime.formatHHMM: String get() = this.format(DateTimeFormatter.ofPattern("HH:mm"))
val OffsetDateTime.formatDateDisplayName: String get() = this.format(DateTimeFormatter.ofPattern("EEE d MMM"))
val OffsetDateTime.formatDateDisplayName2: String get() = this.format(DateTimeFormatter.ofPattern("EEEE d MMMM"))
val LocalDate.formatDateDisplayName2: String get() = this.format(DateTimeFormatter.ofPattern("EEEE d MMMM"))

val DayOfWeekLocalTime.formatDisplayName: String
    get() = String.format("%s: %s",
            this.dayOfWeek.getDisplayName(TextStyle.FULL, Locale.getDefault()).capitalize(),
            this.localTime.formatHHMM)

val ImmutableList<DayOfWeekLocalTime>.formatDisplayNameTime: String
    get() = this.map { " - " + it.formatDisplayName }.joinToString(separator = "\n")


val DayOfWeekHours.formatDisplayName: String
    get() = String.format("%s: %s",
                          this.dayOfWeek.getDisplayName(TextStyle.FULL, Locale.getDefault()).capitalize(),
                          this.hours.formatHoursMinutes)

val ImmutableList<DayOfWeekHours>.formatDisplayName: String
    get() = this.map { " - " + it.formatDisplayName }.joinToString(separator = "\n")

val LocalTime.formatHHMM: String get() = this.format(DateTimeFormatter.ofPattern("HH:mm"))

val Int.formatPercentage: String  get() {
    val prefix = if (this > 0) "+" else if (this < 0) "-" else ""
    return String.format("%s%d%%", prefix, this)
}

val Float.formatHoursMinutes: String
    get() {
        val hours: Int = Math.floor(Math.abs(this).toDouble()).toInt()
        val minutes: Int = Math.floor(((Math.abs(this) - hours) * MINUTES_IN_AN_HOUR).toDouble()).toInt()

        return when {
            hours == 0 && minutes > 0 -> String.format("%dmin", minutes)
            hours > 0 && minutes == 0 -> String.format("%dh", hours)
            hours > 0 && minutes > 0 -> String.format("%dh %dmin", hours, minutes)
            else -> "0h"
        }
    }

val Float.formatHoursMinutes2: String
    get() {
        val hours: Int = Math.floor(Math.abs(this).toDouble()).toInt()
        val minutes: Int = Math.floor(((Math.abs(this) - hours) * MINUTES_IN_AN_HOUR).toDouble()).toInt()
        return when {
            hours == 0 -> String.format("%dmin", minutes)
            else -> String.format("%d h %d min", hours, minutes)
        }
    }

val Float.formatHoursMinutes3: String
    get() {
        val hours: Int = Math.floor(Math.abs(this).toDouble()).toInt()
        val minutes: Int = Math.floor(((Math.abs(this) - hours) * MINUTES_IN_AN_HOUR).toDouble()).toInt()

        return when {
            hours == 0 && minutes > 0 -> String.format("0.%02d", minutes)
            hours > 0 && minutes == 0 -> String.format("%d", hours)
            hours > 0 && minutes > 0 -> String.format("%d.%02d", hours, minutes)
            else -> "0"
        }
    }

val Float.formatHoursMinutesWithPrefix: String
    get() {
        val prefix = if (this > 0) "+" else "-"
        return prefix + this.formatHoursMinutes
    }

val Long.formatTimestamp: String
    get() {
        val date = Date(this)
        val format = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        return format.format(date)
    }