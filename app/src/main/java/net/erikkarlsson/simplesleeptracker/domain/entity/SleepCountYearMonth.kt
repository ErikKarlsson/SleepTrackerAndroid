package net.erikkarlsson.simplesleeptracker.domain.entity

data class SleepCountYearMonth(val sleepCount: Int, val yearString: String, val monthString: String) {
    val year get() = Integer.parseInt(yearString)
    val month get() = Integer.parseInt(monthString)
}