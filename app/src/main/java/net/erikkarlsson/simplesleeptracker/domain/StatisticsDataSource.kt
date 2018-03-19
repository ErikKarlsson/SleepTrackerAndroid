package net.erikkarlsson.simplesleeptracker.domain

import io.reactivex.Observable
import org.threeten.bp.LocalDate

interface StatisticsDataSource {

    /**
     * Get stream of statistics for date range
     */
    fun getStatisticsBetween(startDate: LocalDate, endDate: LocalDate): Observable<Statistics>

    /**
     * Get stream of statistic comparison between two date ranges
     */
    fun getStatisticComparisonBetween(first: DateRange, second: DateRange): Observable<StatisticComparison>

    /**
     * Get stream of statistic comparison between current and previous week
     */
    fun getStatisticComparisonBetweenCurrentAndPreviousWeek(): Observable<StatisticComparison>
}
