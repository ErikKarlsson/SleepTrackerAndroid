package net.erikkarlsson.simplesleeptracker.domain

import io.reactivex.Observable
import net.erikkarlsson.simplesleeptracker.domain.entity.DateRange
import net.erikkarlsson.simplesleeptracker.domain.entity.Sleep
import net.erikkarlsson.simplesleeptracker.domain.entity.SleepCountYearMonth
import net.erikkarlsson.simplesleeptracker.domain.entity.Statistics

interface StatisticsDataSource {

    /**
     * Get stream of overall statistics
     */
    fun getStatistics(): Observable<Statistics>

    /**
     * Get stream of statistics for date range
     */
    fun getStatistics(dateRange: DateRange): Observable<Statistics>

    /**
     * Get stream of sleep count grouped by year and month.
     */
    fun getSleepCountYearMonth(): Observable<List<SleepCountYearMonth>>

    /**
     * Get stream of youngest sleep. Emits empty list if no sleep found.
     */
    fun getYoungestSleep(): Observable<Sleep>

    /**
     * Get stream of oldest sleep. Emits empty list if no sleep found.
     */
    fun getOldestSleep(): Observable<Sleep>
}

