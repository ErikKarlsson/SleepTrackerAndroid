package net.erikkarlsson.simplesleeptracker.domain.entity

data class DayOfWeekHours(val day: Int, val hours: Float) {

    val dayOfWeekIso: Int get() = if (day == 0) 7 else day
}
