package net.erikkarlsson.simplesleeptracker.data

import org.threeten.bp.DayOfWeek

data class DayOfWeekHours(val day: Int, val hours: Float) {
    val dayOfWeek: DayOfWeek get() {
        // SQLite represents day of week 0-6 with Sunday==0
        // Java8 DayOfWeek follows the ISO-8601 standard, from 1 (Monday) to 7 (Sunday)
        val dayOfWeekIso: Int = if (day == 0) 7 else day
        return DayOfWeek.of(dayOfWeekIso)
    }
}