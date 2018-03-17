package net.erikkarlsson.simplesleeptracker.domain

import io.reactivex.Observable
import org.threeten.bp.LocalDate

interface StatisticsDataSource {
    /**
     * Get statistic comparison between two date ranges
     */
    fun getStatistics(): Observable<Statistics>

    /**
     * Get statistics between dates
     */
    fun getStatisticsBetweenDates(startDate: LocalDate, endDate: LocalDate): Observable<Statistics>

    /**
     * Get overall statistics
     */
    fun getStatisticComparison(startDateFirst: LocalDate, endDateFirst: LocalDate, startDateSecond: LocalDate, endDateSecond: LocalDate): Observable<StatisticComparison>
}
