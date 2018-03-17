package net.erikkarlsson.simplesleeptracker.domain

import io.reactivex.Observable
import org.threeten.bp.LocalDate

interface StatisticsDataSource {

    /**
     * Get stream of overall statistics
     */
    fun getStatistics(): Observable<Statistics>

    /**
     * Get stream of statistics for date range
     */
    fun getStatisticsBetweenDates(startDate: LocalDate, endDate: LocalDate): Observable<Statistics>

    /**
     * Get stream of statistic comparison between two date ranges
     */
    fun getStatisticComparison(startDateFirst: LocalDate,
                               endDateFirst: LocalDate,
                               startDateSecond: LocalDate,
                               endDateSecond: LocalDate): Observable<StatisticComparison>
}
