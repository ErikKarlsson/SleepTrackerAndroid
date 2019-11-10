package net.erikkarlsson.simplesleeptracker.domain.entity

import org.threeten.bp.DayOfWeek

data class DayOfWeekHours(val day: Int, val hours: Float) {

    val dayOfWeekIso: Int get() = if (day == 0) 7 else day

    val dayOfWeek: DayOfWeek get() {
        // SQLite represents day of week 0-6 with Sunday==0
        // Java8 DayOfWeek follows the ISO-8601 standard, from 1 (Monday) to 7 (Sunday)
        return DayOfWeek.of(dayOfWeekIso)
    }
}
