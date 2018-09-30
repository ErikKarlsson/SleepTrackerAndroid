package net.erikkarlsson.simplesleeptracker.feature.statistics.domain

import io.reactivex.Observable
import io.reactivex.rxkotlin.Observables
import net.erikkarlsson.simplesleeptracker.domain.StatisticsDataSource
import net.erikkarlsson.simplesleeptracker.domain.entity.StatisticComparison
import net.erikkarlsson.simplesleeptracker.domain.task.ObservableTask
import net.erikkarlsson.simplesleeptracker.feature.statistics.DateRangePair
import net.erikkarlsson.simplesleeptracker.feature.statistics.domain.StatisticComparisonTask.Params
import javax.inject.Inject

/**
 * Get stream of statistic comparison between current and previous week
 */
class StatisticComparisonTask @Inject constructor(
        private val statisticsRepository: StatisticsDataSource)
    : ObservableTask<StatisticComparison, Params> {

    override fun execute(params: Params): Observable<StatisticComparison> {
        val firstRange = params.rangePair.first
        val secondRange = params.rangePair.second

        return Observables.combineLatest(
                statisticsRepository.getStatistics(firstRange),
                statisticsRepository.getStatistics(secondRange))
        { firstWeek, secondWeek -> StatisticComparison(firstWeek, secondWeek) }
    }

    data class Params(val rangePair: DateRangePair)
}