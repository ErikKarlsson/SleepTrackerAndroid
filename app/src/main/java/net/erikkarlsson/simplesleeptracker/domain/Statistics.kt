package net.erikkarlsson.simplesleeptracker.domain

import org.threeten.bp.LocalTime

data class Statistics(val avgSleep: Float,
                      val longestSleep: Float,
                      val shortestSleep: Float,
                      val averageWakeUpTime: LocalTime,
                      val averageBedTime: LocalTime) {
    companion object {
        fun empty() = Statistics(-1.0f, -1.0f, -1.0f, LocalTime.MAX, LocalTime.MAX)
    }
}
