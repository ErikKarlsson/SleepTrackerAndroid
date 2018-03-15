package net.erikkarlsson.simplesleeptracker.domain

import org.threeten.bp.LocalTime

data class Statistics(val avgSleepHours: Float,
                      val longestSleep: Sleep,
                      val shortestSleep: Sleep,
                      val averageWakeUpTime: LocalTime,
                      val averageBedTime: LocalTime) {
    companion object {
        fun empty() = Statistics(-1.0f, Sleep.empty(), Sleep.empty(), LocalTime.MAX, LocalTime.MAX)
    }
}
