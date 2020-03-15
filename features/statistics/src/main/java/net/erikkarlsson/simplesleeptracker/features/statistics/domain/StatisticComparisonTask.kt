package net.erikkarlsson.simplesleeptracker.features.statistics.domain

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import net.erikkarlsson.simplesleeptracker.domain.StatisticsDataSource
import net.erikkarlsson.simplesleeptracker.domain.entity.StatisticComparison
import net.erikkarlsson.simplesleeptracker.domain.task.FlowTask
import net.erikkarlsson.simplesleeptracker.features.statistics.DateRangePair
import net.erikkarlsson.simplesleeptracker.features.statistics.domain.StatisticComparisonTask.Params
import javax.inject.Inject

/**
 * Get stream of statistic comparison between current and previous week
 */
class StatisticComparisonTask @Inject constructor(
        private val statisticsRepository: StatisticsDataSource)
    : FlowTask<StatisticComparison, Params> {

    override fun flow(params: Params): Flow<StatisticComparison> {
        val firstRange = params.rangePair.first
        val secondRange = params.rangePair.second

        val firstFlow = statisticsRepository.getStatistics(firstRange)
        val secondFlow = statisticsRepository.getStatistics(secondRange)

        return firstFlow.combine(secondFlow) { first, second ->
            StatisticComparison(first, second)
        }
    }

    data class Params(val rangePair: DateRangePair)
}
