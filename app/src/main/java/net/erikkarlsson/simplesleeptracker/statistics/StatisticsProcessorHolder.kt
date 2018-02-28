package net.erikkarlsson.simplesleeptracker.statistics

import io.reactivex.ObservableTransformer
import net.erikkarlsson.simplesleeptracker.statistics.processor.LoadStatistics
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class StatisticsProcessorHolder @Inject constructor(private val loadStatistics: LoadStatistics) {

    internal val actionProcessor =
            ObservableTransformer<StatisticsAction, StatisticsResult> { actions ->
                actions.ofType(StatisticsAction.LoadStatisticsAction::class.java).compose(loadStatistics.processor)
            }

}