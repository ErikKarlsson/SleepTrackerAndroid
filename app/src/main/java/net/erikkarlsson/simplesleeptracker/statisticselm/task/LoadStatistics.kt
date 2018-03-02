package net.erikkarlsson.simplesleeptracker.statisticselm.task

import io.reactivex.Single
import net.erikkarlsson.simplesleeptracker.domain.StatisticsDataSource
import net.erikkarlsson.simplesleeptracker.statisticselm.StatisticsMsg
import net.erikkarlsson.simplesleeptracker.util.SchedulerProvider
import javax.inject.Inject

class LoadStatistics @Inject constructor(
        private val statisticsRepository: StatisticsDataSource,
        private val schedulerProvider: SchedulerProvider) {

    internal fun task(): Single<StatisticsMsg> =
            statisticsRepository.getStatistics()
                .map { StatisticsMsg.LoadStatisticsResult.LoadStatisticsSuccess(it) }
                .cast(StatisticsMsg::class.java)
                .onErrorReturn(StatisticsMsg.LoadStatisticsResult::LoadStatisticsFailure)
                .subscribeOn(schedulerProvider.io())
}
