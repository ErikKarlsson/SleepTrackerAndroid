package net.erikkarlsson.simplesleeptracker.domain

import io.reactivex.Observable
import io.reactivex.rxkotlin.Observables
import org.threeten.bp.DayOfWeek
import javax.inject.Inject

/**
 * Get stream of statistic comparison between current and previous week
 */
class StatisticComparisonTask @Inject constructor(
        private val statisticsRepository: StatisticsDataSource,
        private val dateTimeProvider: DateTimeProvider) {

    fun execute(): Observable<StatisticComparison> {
        val now = dateTimeProvider.now().toLocalDate()
        val monday = now.with(DayOfWeek.MONDAY)
        val sunday = now.with(DayOfWeek.SUNDAY)
        val currentWeek = DateRange(monday, sunday)
        val previousWeek = DateRange(
                monday.minusWeeks(1),
                sunday.minusWeeks(1))

        return Observables.combineLatest(
                statisticsRepository.getStatistics(currentWeek).startWith(Statistics.empty()),
                statisticsRepository.getStatistics(previousWeek).startWith(Statistics.empty()))
        { firstWeek, secondWeek -> StatisticComparison(firstWeek, secondWeek) }
    }

}