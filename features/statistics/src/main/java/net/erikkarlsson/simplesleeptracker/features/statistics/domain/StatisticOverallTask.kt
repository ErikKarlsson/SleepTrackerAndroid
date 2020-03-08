package net.erikkarlsson.simplesleeptracker.features.statistics.domain

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import net.erikkarlsson.simplesleeptracker.domain.StatisticsDataSourceCoroutines
import net.erikkarlsson.simplesleeptracker.domain.entity.StatisticComparison
import net.erikkarlsson.simplesleeptracker.domain.entity.Statistics
import net.erikkarlsson.simplesleeptracker.domain.task.FlowTask
import net.erikkarlsson.simplesleeptracker.domain.task.ObservableTask
import javax.inject.Inject

/**
 * Get stream of overall statistic, with no statistics to compare against.
 */
class StatisticOverallTask @Inject constructor(
        private val statisticsRepository: StatisticsDataSourceCoroutines)
    : FlowTask<StatisticComparison, ObservableTask.None> {

    override fun flow(params: ObservableTask.None): Flow<StatisticComparison> {
        return statisticsRepository.getStatistics()
                .map { StatisticComparison(it, Statistics.empty()) }
    }

}
