package net.erikkarlsson.simplesleeptracker.domain

import net.erikkarlsson.simplesleeptracker.util.formatDiffHHMM
import net.erikkarlsson.simplesleeptracker.util.formatDiffPercentage
import net.erikkarlsson.simplesleeptracker.util.formatPercentageDiff

data class StatisticComparison(val first: Statistics, val second: Statistics) {

    val avgSleepDiffPercentage: String get() = first.avgSleepHours.formatDiffPercentage(second.avgSleepHours)
    val avgSleepDiffHHMM: String get() = first.avgSleepHours.formatDiffHHMM(second.avgSleepHours)
    val avgBedTimeDiffHHMM: String get() = first.averageBedTime.formatDiffHHMM(second.averageBedTime)
    val avgWakeUpTimeDiffHHMM: String get() = first.averageWakeUpTime.formatDiffHHMM(second.averageWakeUpTime)
    val timeSleepingDiffPercentage: String get() = first.timeSleeping.formatPercentageDiff(second.timeSleeping)

    companion object {
        fun empty() = StatisticComparison(Statistics.empty(), Statistics.empty())
    }

}