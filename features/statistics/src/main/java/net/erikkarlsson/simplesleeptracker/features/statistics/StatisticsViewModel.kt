package net.erikkarlsson.simplesleeptracker.features.statistics

import com.airbnb.mvrx.FragmentViewModelContext
import com.airbnb.mvrx.MvRxViewModelFactory
import com.airbnb.mvrx.Success
import com.airbnb.mvrx.ViewModelContext
import com.squareup.inject.assisted.Assisted
import com.squareup.inject.assisted.AssistedInject
import io.reactivex.rxkotlin.zipWith
import net.erikkarlsson.simplesleeptracker.core.MvRxViewModel
import net.erikkarlsson.simplesleeptracker.domain.PREFS_SELECTED_COMPARE_FILTER
import net.erikkarlsson.simplesleeptracker.domain.PREFS_SELECTED_FILTER
import net.erikkarlsson.simplesleeptracker.domain.PreferencesDataSource
import net.erikkarlsson.simplesleeptracker.domain.StatisticsDataSource
import net.erikkarlsson.simplesleeptracker.domain.entity.DateRange
import net.erikkarlsson.simplesleeptracker.domain.entity.Sleep

class StatisticsViewModel @AssistedInject constructor(
        @Assisted val initialState: StatisticsState,
        private val statisticsDataSource: StatisticsDataSource,
        private val preferences: PreferencesDataSource)
    : MvRxViewModel<StatisticsState>(initialState) {

    init {
        subscribeOldestYoungest()
        subscribeSavedFilters()
    }

    fun statisticsFilterSelected(filter: StatisticsFilter) {
        withState {
            val compareFilter = it.compareFilter
            val youngest = it.youngest
            val oldest = it.oldest
            val dateRanges = getDateRanges(filter, compareFilter, youngest, oldest)
            setState {
                copy(filter = filter, dateRanges = dateRanges)
            }
        }

        preferences.set(PREFS_SELECTED_FILTER, filter.ordinal)
    }

    fun compareFilterSelected(compareFilter: CompareFilter) {
        withState {
            val dateRanges = getDateRanges(it.filter, compareFilter, it.youngest, it.oldest)
            setState {
                copy(compareFilter = compareFilter, dateRanges = dateRanges)
            }
        }

        preferences.set(PREFS_SELECTED_COMPARE_FILTER, compareFilter.ordinal)
    }

    private fun subscribeOldestYoungest() {
        statisticsDataSource.getYoungestSleep()
                .zipWith(statisticsDataSource.getOldestSleep())
                .map { YoungestOldest(it.first, it.second) }
                .execute {
                    if (it is Success) {
                        val filter = this.filter
                        val compareFilter = this.compareFilter
                        val youngest = it()!!.youngest
                        val oldest = it()!!.oldest
                        val sleepFound = youngest != Sleep.empty()

                        val dateRanges = if (sleepFound) {
                            getDateRanges(filter, compareFilter, youngest, oldest)
                        } else {
                            listOf()
                        }

                        copy(youngest = youngest, oldest = oldest, dateRanges = dateRanges,
                                isLoading = false, sleepFound = sleepFound)
                    } else {
                        copy()
                    }
                }
    }

    fun subscribeSavedFilters() {
        preferences.getInt(PREFS_SELECTED_FILTER)
                .zipWith(preferences.getInt(PREFS_SELECTED_COMPARE_FILTER))
                .take(1)
                .map {
                    val statisticsFilter = StatisticsFilter.values()[it.first]
                    val compareFilter = CompareFilter.values()[it.second]
                    SavedFilters(statisticsFilter, compareFilter)
                }
                .execute {
                    if (it is Success) {
                        val savedFilterLoaded = it()
                        copy(filter = it()!!.filter, compareFilter = savedFilterLoaded!!.compareFilter)
                    } else {
                        copy()
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

    @AssistedInject.Factory
    interface Factory {
        fun create(initialState: StatisticsState): StatisticsViewModel
    }

    companion object : MvRxViewModelFactory<StatisticsViewModel, StatisticsState> {
        override fun create(viewModelContext: ViewModelContext, state: StatisticsState): StatisticsViewModel? {
            val fragment = (viewModelContext as FragmentViewModelContext).fragment<StatisticsFragment>()
            return fragment.viewModelFactory.create(state)
        }
    }
}
