package net.erikkarlsson.simplesleeptracker.feature.statistics

import com.airbnb.mvrx.MvRxState
import net.erikkarlsson.simplesleeptracker.domain.entity.DateRange
import net.erikkarlsson.simplesleeptracker.domain.entity.Sleep

typealias DateRangePair = Pair<DateRange, DateRange>

data class SavedFilters(val filter: StatisticsFilter, val compareFilter: CompareFilter)

data class YoungestOldest(val youngest: Sleep, val oldest: Sleep)

data class StatisticsState(val filter: StatisticsFilter = StatisticsFilter.OVERALL,
                           val compareFilter: CompareFilter = CompareFilter.PREVIOUS,
                           val youngest: Sleep = Sleep.empty(),
                           val oldest: Sleep = Sleep.empty(),
                           val dateRanges: List<DateRangePair> = listOf(),
                           val sleepFound: Boolean = false,
                           val isLoading: Boolean = true) : MvRxState {

    val shouldShowEmptyState: Boolean = !isLoading && !sleepFound

    val filterOrdinal get() = filter.ordinal

    val compareFilterOrdinal get() = compareFilter.ordinal

    val shouldShowTabs = filter != StatisticsFilter.OVERALL && sleepFound

    val shouldShowCompareFilter = filter != StatisticsFilter.OVERALL

    companion object {
        fun empty() = StatisticsState()
    }
}
