package net.erikkarlsson.simplesleeptracker.testutil

import net.erikkarlsson.simplesleeptracker.domain.entity.Statistics

fun mockStatistics(sleepCount: Int): Statistics =
        Statistics.empty().copy(sleepCount = sleepCount)
