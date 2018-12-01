package net.erikkarlsson.simplesleeptracker.feature.statistics

import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.rxkotlin.zipWith
import net.erikkarlsson.simplesleeptracker.domain.PREFS_SELECTED_COMPARE_FILTER
import net.erikkarlsson.simplesleeptracker.domain.PREFS_SELECTED_FILTER
import net.erikkarlsson.simplesleeptracker.domain.PreferencesDataSource
import net.erikkarlsson.simplesleeptracker.domain.StatisticsDataSource
import net.erikkarlsson.simplesleeptracker.domain.entity.DateRange
import net.erikkarlsson.simplesleeptracker.domain.entity.Sleep
import net.erikkarlsson.simplesleeptracker.elm.*
import net.erikkarlsson.simplesleeptracker.feature.statistics.DateRanges.getMonthDateRanges
import net.erikkarlsson.simplesleeptracker.feature.statistics.DateRanges.getOverallDateRange
import net.erikkarlsson.simplesleeptracker.feature.statistics.DateRanges.getWeekDateRanges
import net.erikkarlsson.simplesleeptracker.feature.statistics.DateRanges.getYearDateRanges
import javax.inject.Inject

typealias DateRangePair = Pair<DateRange, DateRange>

class StatisticsComponent @Inject
constructor(private val youngestOldestSubscription: YoungestOldestSubscription,
            private val savedFilterSubscription: SavedFilterSubscription,
            private val preferencesDataSource: PreferencesDataSource)
    : Component<StatisticsState, StatisticsMsg, StatisticsCmd> {

    override fun call(cmd: StatisticsCmd): Single<StatisticsMsg> {
            when (cmd) {
                is SaveFilter ->
                    preferencesDataSource.set(PREFS_SELECTED_FILTER, cmd.filter.ordinal)
                is SaveCompareFilter ->
                    preferencesDataSource.set(PREFS_SELECTED_COMPARE_FILTER, cmd.compareFilter.ordinal)
            }

        return Single.just(DoNothing)
    }

    override fun initState(): StatisticsState = StatisticsState.empty()

    override fun subscriptions(): List<Sub<StatisticsState, StatisticsMsg>> =
            listOf(youngestOldestSubscription, savedFilterSubscription)

    override fun update(msg: StatisticsMsg, prevState: StatisticsState): Pair<StatisticsState, StatisticsCmd?> =
            when (msg) {
                is StatisticsFilterSelected -> onFilterSelected(prevState, msg)
                is YoungestOldestLoaded -> onOldestYoungestLoaded(prevState, msg)
                is SavedFilterLoaded -> onSavedFilterLoaded(prevState, msg)
                is CompareFilterSelected -> onCompareFilterSelected(prevState, msg)
                DoNothing -> prevState.noCmd()
            }

    private fun onSavedFilterLoaded(prevState: StatisticsState, msg: SavedFilterLoaded): Pair<StatisticsState, StatisticsCmd?> =
            prevState.copy(filter = msg.filter, compareFilter = msg.compareFilter).noCmd()

    private fun onOldestYoungestLoaded(prevState: StatisticsState, msg: YoungestOldestLoaded): Pair<StatisticsState, StatisticsCmd?> {
        val filter = prevState.filter
        val compareFilter = prevState.compareFilter
        val youngest = msg.youngest
        val oldest = msg.oldest
        val sleepFound = youngest != Sleep.empty()

        val dateRanges = if (sleepFound) {
            getDateRanges(filter, compareFilter, youngest, oldest)
        } else {
            listOf()
        }

        return prevState.copy(youngest = youngest, oldest = oldest,
                dateRanges = dateRanges, isLoading = false).noCmd()
    }

    private fun onFilterSelected(prevState: StatisticsState, msg: StatisticsFilterSelected): Pair<StatisticsState, StatisticsCmd?> {
        val filter = msg.filter
        val compareFilter = prevState.compareFilter
        val youngest = prevState.youngest
        val oldest = prevState.oldest
        val dateRanges = getDateRanges(filter, compareFilter, youngest, oldest)

        return prevState.copy(filter = filter, dateRanges = dateRanges) withCmd SaveFilter(filter)
    }

    private fun onCompareFilterSelected(prevState: StatisticsState, msg: CompareFilterSelected): Pair<StatisticsState, StatisticsCmd?> {
        val compareFilter = msg.compareFilter
        val filter = prevState.filter
        val youngest = prevState.youngest
        val oldest = prevState.oldest
        val dateRanges = getDateRanges(filter, compareFilter, youngest, oldest)

        return prevState.copy(compareFilter = compareFilter,
                dateRanges = dateRanges) withCmd SaveCompareFilter(compareFilter)
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
            StatisticsFilter.OVERALL -> getOverallDateRange()
            StatisticsFilter.WEEK -> getWeekDateRanges(startEndDateRange, compareFilter)
            StatisticsFilter.MONTH -> getMonthDateRanges(startEndDateRange, compareFilter)
            StatisticsFilter.YEAR -> getYearDateRanges(startEndDateRange, compareFilter)
            StatisticsFilter.NONE -> listOf()
        }
    }

}

// Subscription
class YoungestOldestSubscription @Inject constructor(private val statisticsDataSource: StatisticsDataSource) : StatelessSub<StatisticsState, StatisticsMsg>() {
    override fun invoke(): Observable<StatisticsMsg> =
            statisticsDataSource.getYoungestSleep()
                    .zipWith(statisticsDataSource.getOldestSleep())
                    .map { YoungestOldestLoaded(it.first, it.second) }
}

class SavedFilterSubscription @Inject constructor(private val preferences: PreferencesDataSource) : StatelessSub<StatisticsState, StatisticsMsg>() {
    override fun invoke(): Observable<StatisticsMsg> =
            preferences.getInt(PREFS_SELECTED_FILTER)
                    .zipWith(preferences.getInt(PREFS_SELECTED_COMPARE_FILTER))
                    .take(1)
                    .map {
                        val statisticsFilter = StatisticsFilter.values()[it.first]
                        val compareFilter = CompareFilter.values()[it.second]
                        SavedFilterLoaded(statisticsFilter, compareFilter)
                    }
}

// State
data class StatisticsState(val filter: StatisticsFilter,
                           val compareFilter: CompareFilter,
                           val youngest: Sleep,
                           val oldest: Sleep,
                           val dateRanges: List<DateRangePair>,
                           val isLoading: Boolean) : State {

    val isEmpty = dateRanges.size == 0

    val filterOrdinal get() = filter.ordinal

    val compareFilterOrdinal get() = compareFilter.ordinal

    val shouldShowTabs = filter != StatisticsFilter.OVERALL && !isEmpty

    val shouldShowCompareFilter = filter != StatisticsFilter.OVERALL

    companion object {
        fun empty() = StatisticsState(StatisticsFilter.OVERALL, CompareFilter.PREVIOUS, Sleep.empty(), Sleep.empty(), listOf(), true)
    }
}

// Msg
sealed class StatisticsMsg : Msg

data class StatisticsFilterSelected(val filter: StatisticsFilter) : StatisticsMsg()

data class CompareFilterSelected(val compareFilter: CompareFilter) : StatisticsMsg()

data class SavedFilterLoaded(val filter: StatisticsFilter, val compareFilter: CompareFilter) : StatisticsMsg()

data class YoungestOldestLoaded(val youngest: Sleep, val oldest: Sleep) : StatisticsMsg()

object DoNothing : StatisticsMsg()

// Cmd
sealed class StatisticsCmd : Cmd

data class SaveFilter(val filter: StatisticsFilter) : StatisticsCmd()
data class SaveCompareFilter(val compareFilter: CompareFilter) : StatisticsCmd()
