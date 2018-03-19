package net.erikkarlsson.simplesleeptracker.domain

import io.reactivex.Observable

interface StatisticsDataSource {

    /**
     * Get stream of statistics for date range
     */
    fun getStatistics(dateRange: DateRange): Observable<Statistics>

}
