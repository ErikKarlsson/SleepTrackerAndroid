package net.erikkarlsson.simplesleeptracker.features.statistics.item

import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import net.erikkarlsson.simplesleeptracker.core.ReduxViewModel
import net.erikkarlsson.simplesleeptracker.domain.entity.StatisticComparison
import net.erikkarlsson.simplesleeptracker.domain.entity.Statistics
import net.erikkarlsson.simplesleeptracker.domain.task.FlowTask
import net.erikkarlsson.simplesleeptracker.features.statistics.DateRangePair
import net.erikkarlsson.simplesleeptracker.features.statistics.StatisticsFilter
import net.erikkarlsson.simplesleeptracker.features.statistics.domain.StatisticComparisonTask
import net.erikkarlsson.simplesleeptracker.features.statistics.domain.StatisticOverallTask
import javax.inject.Inject

data class StatisticsItemState(val statistics: StatisticComparison? = null) {

    val isStatisticsEmpty
        get() = statistics?.first == Statistics.empty()
}

@HiltViewModel
class StatisticsItemViewModel @Inject constructor(
        private val statisticOverallTask: StatisticOverallTask,
        private val statisticComparisonTask: StatisticComparisonTask)
    : ReduxViewModel<StatisticsItemState>(StatisticsItemState()) {

    fun loadStatistics(dataRangePair: DateRangePair, filter: StatisticsFilter) {
        viewModelScope.launch {
            when (filter) {
                StatisticsFilter.OVERALL -> {
                    statisticOverallTask.flow(FlowTask.None())
                            .collectAndSetState { copy(statistics = it) }
                }
                else -> {
                    val params = StatisticComparisonTask.Params(dataRangePair)
                    statisticComparisonTask.flow(params)
                            .collectAndSetState { copy(statistics = it) }
                }
            }
        }
    }

}

