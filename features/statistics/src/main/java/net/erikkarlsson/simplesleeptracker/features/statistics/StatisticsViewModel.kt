package net.erikkarlsson.simplesleeptracker.features.statistics

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.flow.zip
import kotlinx.coroutines.launch
import net.erikkarlsson.simplesleeptracker.core.ReduxViewModel
import net.erikkarlsson.simplesleeptracker.domain.PREFS_SELECTED_COMPARE_FILTER
import net.erikkarlsson.simplesleeptracker.domain.PREFS_SELECTED_FILTER
import net.erikkarlsson.simplesleeptracker.domain.PreferencesDataSource
import net.erikkarlsson.simplesleeptracker.domain.StatisticsDataSource
import net.erikkarlsson.simplesleeptracker.domain.entity.DateRange
import net.erikkarlsson.simplesleeptracker.domain.entity.Sleep

class StatisticsViewModel @ViewModelInject constructor(
        private val statisticsDataSource: StatisticsDataSource,
        private val preferences: PreferencesDataSource)
    : ReduxViewModel<StatisticsState>(StatisticsState()) {

    init {
        subscribeOldestYoungest()
        subscribeSavedFilters()
    }

    fun statisticsFilterSelected(filter: StatisticsFilter) {
        viewModelScope.withState {
            val compareFilter = it.compareFilter
            val youngest = it.youngest
            val oldest = it.oldest
            val dateRanges = getDateRanges(filter, compareFilter, youngest, oldest)

            viewModelScope.launchSetState {
                copy(filter = filter, dateRanges = dateRanges)
            }
        }

        preferences.set(PREFS_SELECTED_FILTER, filter.ordinal)
    }

    fun compareFilterSelected(compareFilter: CompareFilter) {
        viewModelScope.withState {
            val dateRanges = getDateRanges(it.filter, compareFilter, it.youngest, it.oldest)
            viewModelScope.launchSetState {
                copy(compareFilter = compareFilter, dateRanges = dateRanges)
            }
        }

        preferences.set(PREFS_SELECTED_COMPARE_FILTER, compareFilter.ordinal)
    }

    private fun subscribeOldestYoungest() {
        viewModelScope.launch {
            statisticsDataSource.getYoungestSleep()
                    .zip(statisticsDataSource.getOldestSleep()) { first, second ->
                        YoungestOldest(first, second)
                    }
                    .collectAndSetState {
                        val filter = this.filter
                        val compareFilter = this.compareFilter
                        val youngest = it.youngest
                        val oldest = it.oldest
                        val sleepFound = youngest != Sleep.empty()

                        val dateRanges = if (sleepFound) {
                            getDateRanges(filter, compareFilter, youngest, oldest)
                        } else {
                            listOf()
                        }

                        copy(youngest = youngest, oldest = oldest, dateRanges = dateRanges,
                                isLoading = false, sleepFound = sleepFound)
                    }
        }
    }

    fun subscribeSavedFilters() {
        viewModelScope.launch {
            preferences.getInt(PREFS_SELECTED_FILTER)
                    .zip(preferences.getInt(PREFS_SELECTED_COMPARE_FILTER)) { selectedFilter, selectedCompareFilter ->
                        val statisticsFilter = StatisticsFilter.values()[selectedFilter]
                        val compareFilter = CompareFilter.values()[selectedCompareFilter]
                        SavedFilters(statisticsFilter, compareFilter)
                    }
                    .take(1)
                    .collectAndSetState {
                        copy(filter = it.filter, compareFilter = it.compareFilter)
                    }
        }
    }

    private fun getDateRanges(statisticFilter: StatisticsFilter,
                              compareFilter: CompareFilter,
                              youngest: Sleep,
                              oldest: Sleep): List<DateRangePair> {
        if (youngest == Sleep.empty() || oldest == Sleep.empty()) {
            return listOf()
        }

        val startDate = youngest.toDate?.toLocalDate()
        val endDate = oldest.toDate?.toLocalDate()

        if (startDate == null || endDate == null) {
            throw IllegalStateException("Youngest or oldest sleep null toDate")
        }

        val startEndDateRange = DateRange(startDate, endDate)

        return when (statisticFilter) {
            StatisticsFilter.OVERALL -> DateRanges.getOverallDateRange()
            StatisticsFilter.WEEK -> DateRanges.getWeekDateRanges(startEndDateRange, compareFilter)
            StatisticsFilter.MONTH -> DateRanges.getMonthDateRanges(startEndDateRange, compareFilter)
            StatisticsFilter.YEAR -> DateRanges.getYearDateRanges(startEndDateRange, compareFilter)
            StatisticsFilter.NONE -> listOf()
        }
    }
}
