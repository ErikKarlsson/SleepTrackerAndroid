package net.erikkarlsson.simplesleeptracker.domain

import io.reactivex.Observable
import net.erikkarlsson.simplesleeptracker.domain.entity.DateRange
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
}
