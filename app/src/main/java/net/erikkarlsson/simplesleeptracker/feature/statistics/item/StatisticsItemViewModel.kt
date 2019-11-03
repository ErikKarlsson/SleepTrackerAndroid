package net.erikkarlsson.simplesleeptracker.feature.statistics.item

import com.airbnb.mvrx.*
import com.squareup.inject.assisted.Assisted
import com.squareup.inject.assisted.AssistedInject
import net.erikkarlsson.simplesleeptracker.MvRxViewModel
import net.erikkarlsson.simplesleeptracker.domain.entity.StatisticComparison
import net.erikkarlsson.simplesleeptracker.domain.entity.Statistics
import net.erikkarlsson.simplesleeptracker.domain.task.ObservableTask
import net.erikkarlsson.simplesleeptracker.feature.statistics.DateRangePair
import net.erikkarlsson.simplesleeptracker.feature.statistics.StatisticsFilter
import net.erikkarlsson.simplesleeptracker.feature.statistics.domain.StatisticComparisonTask
import net.erikkarlsson.simplesleeptracker.feature.statistics.domain.StatisticOverallTask

data class StatisticsItemState(val statistics: Async<StatisticComparison> = Uninitialized) : MvRxState {

    val isStatisticsEmpty
        get() = statistics.invoke()?.first == Statistics.empty()
}

class StatisticsItemViewModel @AssistedInject constructor(
        @Assisted val initialState: StatisticsItemState,
        private val statisticOverallTask: StatisticOverallTask,
        private val statisticComparisonTask: StatisticComparisonTask)
    : MvRxViewModel<StatisticsItemState>(initialState) {

    fun loadStatistics(dataRangePair: DateRangePair, filter: StatisticsFilter) {
        when (filter) {
            StatisticsFilter.OVERALL -> {
                statisticOverallTask.observable(ObservableTask.None())
                        .execute { copy(statistics = it) }
            }
            else -> {
                val params = StatisticComparisonTask.Params(dataRangePair)
                statisticComparisonTask.observable(params)
                        .execute { copy(statistics = it) }
            }
        }
    }

    @AssistedInject.Factory
    interface Factory {
        fun create(initialState: StatisticsItemState): StatisticsItemViewModel
    }

    companion object : MvRxViewModelFactory<StatisticsItemViewModel, StatisticsItemState> {
        override fun create(viewModelContext: ViewModelContext, state: StatisticsItemState): StatisticsItemViewModel? {
            val fragment = (viewModelContext as FragmentViewModelContext).fragment<StatisticsItemFragment>()
            return fragment.viewModelFactory.create(state)
        }
    }

}

