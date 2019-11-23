package net.erikkarlsson.simplesleeptracker.domain.entity

import com.google.common.collect.ImmutableList
import net.easypark.dateutil.formatHoursMinutes
import org.threeten.bp.DayOfWeek
import org.threeten.bp.format.TextStyle
import java.util.*

data class DayOfWeekHours(val day: Int, val hours: Float) {

    val dayOfWeekIso: Int get() = if (day == 0) 7 else day

    val dayOfWeek: DayOfWeek get() {
        // SQLite represents day of week 0-6 with Sunday==0
        // Java8 DayOfWeek follows the ISO-8601 standard, from 1 (Monday) to 7 (Sunday)
        return DayOfWeek.of(dayOfWeekIso)
    }
}

val DayOfWeekHours.formatDisplayName: String
    get() = String.format("%s: %s",
            this.dayOfWeek.getDisplayName(TextStyle.FULL, Locale.getDefault()).capitalize(),
            this.hours.formatHoursMinutes)

val ImmutableList<DayOfWeekHours>.formatDisplayName: String
    get() = this.map { " - " + it.formatDisplayName }.joinToString(separator = "\n")
