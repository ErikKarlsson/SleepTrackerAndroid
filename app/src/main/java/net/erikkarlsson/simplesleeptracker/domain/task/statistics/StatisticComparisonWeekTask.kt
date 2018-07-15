package net.erikkarlsson.simplesleeptracker.domain.task.statistics

import io.reactivex.Observable
import io.reactivex.rxkotlin.Observables
import net.erikkarlsson.simplesleeptracker.domain.DateTimeProvider
import net.erikkarlsson.simplesleeptracker.domain.StatisticsDataSource
import net.erikkarlsson.simplesleeptracker.domain.entity.DateRange
import net.erikkarlsson.simplesleeptracker.domain.entity.StatisticComparison
import net.erikkarlsson.simplesleeptracker.domain.entity.Statistics
import net.erikkarlsson.simplesleeptracker.domain.task.ObservableTask
import org.threeten.bp.DayOfWeek
import javax.inject.Inject

/**
 * Get stream of statistic comparison between current and previous week
 */
class StatisticComparisonWeekTask @Inject constructor(
        private val statisticsRepository: StatisticsDataSource,
        private val dateTimeProvider: DateTimeProvider)
    : ObservableTask<StatisticComparison, ObservableTask.None> {

    override fun execute(params: ObservableTask.None): Observable<StatisticComparison> {
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