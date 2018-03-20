package net.erikkarlsson.simplesleeptracker.domain

import net.erikkarlsson.simplesleeptracker.util.diffHours

data class StatisticComparison(val first: Statistics, val second: Statistics) {

    val avgSleepDiffHours: Float get() = first.avgSleepHours - second.avgSleepHours
    val avgBedTimeDiffHours: Float get() = first.averageBedTime.diffHours(second.averageBedTime)
    val avgWakeUpTimeDiffHours: Float get() = first.averageWakeUpTime.diffHours(second.averageWakeUpTime)
    val timeSleepingDiffPercentage: Int get() = first.timeSleepingPercentage - second.timeSleepingPercentage

    companion object {
        fun empty() = StatisticComparison(Statistics.empty(), Statistics.empty())
    }

}