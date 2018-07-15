package net.erikkarlsson.simplesleeptracker.domain.task.statistics

import io.reactivex.Observable
import net.erikkarlsson.simplesleeptracker.domain.StatisticsDataSource
import net.erikkarlsson.simplesleeptracker.domain.entity.StatisticComparison
import net.erikkarlsson.simplesleeptracker.domain.entity.Statistics
import net.erikkarlsson.simplesleeptracker.domain.task.ObservableTask
import javax.inject.Inject

/**
 * Get stream of overall statistic, with no statistics to compare against.
 */
class StatisticComparisonOverallTask @Inject constructor(
        private val statisticsRepository: StatisticsDataSource)
    : ObservableTask<StatisticComparison, ObservableTask.None> {

    override fun execute(params: ObservableTask.None): Observable<StatisticComparison> {
        return statisticsRepository.getStatistics().startWith(Statistics.empty())
            .map { StatisticComparison(it, Statistics.empty()) }
    }

}