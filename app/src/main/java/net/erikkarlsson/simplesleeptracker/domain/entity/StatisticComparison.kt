package net.erikkarlsson.simplesleeptracker.domain.entity

import net.erikkarlsson.simplesleeptracker.util.diffHours

data class StatisticComparison(val first: Statistics, val second: Statistics) {

    val avgSleepDiffHours: Float get() = if (second.isEmpty) 0.0f else first.avgSleepHours - second.avgSleepHours
    val avgBedTimeDiffHours: Float get() = if (second.isEmpty) 0.0f else first.averageBedTime.diffHours(second.averageBedTime)
    val avgWakeUpTimeDiffHours: Float get() = if (second.isEmpty) 0.0f else first.averageWakeUpTime.diffHours(second.averageWakeUpTime)
    val timeSleepingDiffPercentage: Int get() = if (second.isEmpty) 0 else first.timeSleepingPercentage - second.timeSleepingPercentage

    companion object {
        fun empty() = StatisticComparison(Statistics.empty(), Statistics.empty())
    }

}