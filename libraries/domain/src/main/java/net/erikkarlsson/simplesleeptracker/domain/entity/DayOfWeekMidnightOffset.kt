package net.erikkarlsson.simplesleeptracker.domain.entity

import net.easypark.dateutil.midnightOffsetToLocalTime
import org.threeten.bp.DayOfWeek

data class DayOfWeekMidnightOffset(val day: Int, val midnightOffsetInSeconds: Int) {
    val toDayOfWeekLocalTime: DayOfWeekLocalTime
        get() {
            // SQLite represents day of week 0-6 with Sunday==0
            // Java8 DayOfWeek follows the ISO-8601 standard, from 1 (Monday) to 7 (Sunday)
            val dayOfWeekIso: Int = if (day == 0) 7 else day
            return DayOfWeekLocalTime(DayOfWeek.of(dayOfWeekIso), midnightOffsetInSeconds.midnightOffsetToLocalTime)
        }
}
