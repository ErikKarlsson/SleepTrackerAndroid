package net.erikkarlsson.simplesleeptracker.base

import net.erikkarlsson.simplesleeptracker.domain.entity.Statistics

fun mockStatistics(sleepCount: Int): Statistics =
        Statistics.empty().copy(sleepCount = sleepCount)