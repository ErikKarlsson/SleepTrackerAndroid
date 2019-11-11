package net.erikkarlsson.simplesleeptracker.features.statistics

data class StatisticsItemData(val dataRanges: List<DateRangePair>,
                              val filter: StatisticsFilter,
                              val isEmptyState: Boolean = false) {
    companion object {
        fun empty(): StatisticsItemData = StatisticsItemData(emptyList(), StatisticsFilter.OVERALL)
        fun emptyState(): StatisticsItemData = StatisticsItemData(DateRanges.getOverallDateRange(), StatisticsFilter.OVERALL, true)
    }
}
