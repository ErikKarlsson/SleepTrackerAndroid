package net.erikkarlsson.simplesleeptracker.domain

import io.reactivex.Observable
import org.threeten.bp.LocalDate

interface StatisticsDataSource {
    fun getStatistics(): Observable<Statistics>
    fun getStatisticsBetweenDates(startDate: LocalDate, endDate: LocalDate): Observable<Statistics>
}
