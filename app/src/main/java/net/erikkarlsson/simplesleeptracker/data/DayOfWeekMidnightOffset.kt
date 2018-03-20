package net.erikkarlsson.simplesleeptracker.data

import net.erikkarlsson.simplesleeptracker.domain.DayOfWeekLocalTime
import net.erikkarlsson.simplesleeptracker.util.localTime
import org.threeten.bp.DayOfWeek

data class DayOfWeekMidnightOffset(val dayOfWeek: Int, val midnightOffsetInSeconds: Int) {
    val toDayOfWeekLocalTime: DayOfWeekLocalTime
        get() {
            // SQLite represents day of week 0-6 with Sunday==0
            // Java8 DayOfWeek follows the ISO-8601 standard, from 1 (Monday) to 7 (Sunday)
            val dayOfWeekIso: Int = if (dayOfWeek == 0) 7 else dayOfWeek
            return DayOfWeekLocalTime(DayOfWeek.of(dayOfWeekIso), midnightOffsetInSeconds.localTime)
        }
}