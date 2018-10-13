package net.erikkarlsson.simplesleeptracker.feature.statistics

data class StatisticsItemData(val dataRanges: List<DateRangePair>,
                              val filter: StatisticsFilter) {
    companion object {
        fun empty(): StatisticsItemData = StatisticsItemData(emptyList(), StatisticsFilter.OVERALL)
    }
}
