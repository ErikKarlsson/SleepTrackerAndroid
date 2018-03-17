package net.erikkarlsson.simplesleeptracker.domain

import org.threeten.bp.LocalTime

data class Statistics(val sleepCount: Int,
                      val avgSleepHours: Float,
                      val longestSleep: Sleep,
                      val shortestSleep: Sleep,
                      val averageWakeUpTime: LocalTime,
                      val averageBedTime: LocalTime,
                      val averageBedTimeDayOfWeek: List<DayOfWeekLocalTime>,
                      val averageWakeupTimeDayOfWeek: List<DayOfWeekLocalTime>) {

    val timeSleeping get(): Int = Math.min(Math.round(avgSleepHours / 24 * 100), 100)

    companion object {
        fun empty() = Statistics(0, 0.0f, Sleep.empty(), Sleep.empty(), LocalTime.MAX, LocalTime.MAX, emptyList(), emptyList())
    }
}
