package net.erikkarlsson.simplesleeptracker.statistics.processor

import io.reactivex.ObservableTransformer
import net.erikkarlsson.simplesleeptracker.data.StatisticsRepository
import net.erikkarlsson.simplesleeptracker.statistics.StatisticsAction
import net.erikkarlsson.simplesleeptracker.statistics.StatisticsResult
import net.erikkarlsson.simplesleeptracker.util.SchedulerProvider
import javax.inject.Inject

class LoadStatistics @Inject constructor(
        private val statisticsRepository: StatisticsRepository,
        private val schedulerProvider: SchedulerProvider) {

    internal val processor =
            ObservableTransformer<StatisticsAction.LoadStatisticsAction, StatisticsResult.LoadStatisticsResult> { actions ->
                actions.flatMap {
                    statisticsRepository.getStatistics().toObservable()
                        .map { StatisticsResult.LoadStatisticsResult.Success(it) }
                        .cast(StatisticsResult.LoadStatisticsResult::class.java)
                        .onErrorReturn(StatisticsResult.LoadStatisticsResult::Failure)
                        .subscribeOn(schedulerProvider.io())
                }
            }
}
