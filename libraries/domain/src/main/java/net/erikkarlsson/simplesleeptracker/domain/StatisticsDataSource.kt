package net.erikkarlsson.simplesleeptracker.domain

import kotlinx.coroutines.flow.Flow
import net.erikkarlsson.simplesleeptracker.domain.entity.DateRange
import net.erikkarlsson.simplesleeptracker.domain.entity.Sleep
import net.erikkarlsson.simplesleeptracker.domain.entity.SleepCountYearMonth
import net.erikkarlsson.simplesleeptracker.domain.entity.Statistics

interface StatisticsDataSource {

    /**
     * Get stream of overall statistics
     */
    fun getStatistics(): Flow<Statistics>

    /**
     * Get stream of statistics for date range
     */
    fun getStatistics(dateRange: DateRange): Flow<Statistics>

    /**
     * Get stream of sleep count grouped by year and month.
     */
    fun getSleepCountYearMonth(): Flow<List<SleepCountYearMonth>>

    /**
     * Get stream of youngest sleep. Emits empty list if no sleep found.
     */
    fun getYoungestSleep(): Flow<Sleep>

    /**
     * Get stream of oldest sleep. Emits empty list if no sleep found.
     */
    fun getOldestSleep(): Flow<Sleep>
}

