package net.erikkarlsson.simplesleeptracker.feature.statistics

import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.rxkotlin.zipWith
import net.erikkarlsson.simplesleeptracker.domain.StatisticsDataSource
import net.erikkarlsson.simplesleeptracker.domain.entity.DateRange
import net.erikkarlsson.simplesleeptracker.domain.entity.Sleep
import net.erikkarlsson.simplesleeptracker.elm.*
import net.erikkarlsson.simplesleeptracker.util.weekOfWeekBasedYear
import org.threeten.bp.DayOfWeek
import org.threeten.bp.LocalDate
import javax.inject.Inject

typealias DateRangePair = Pair<DateRange, DateRange>

class StatisticsComponent @Inject constructor(private val youngestOldestSubscription: YoungestOldestSubscription)
    : Component<StatisticsState, StatisticsMsg, StatisticsCmd> {

    override fun call(cmd: StatisticsCmd): Single<StatisticsMsg> =
            Single.just(DoNothing)

    override fun initState(): StatisticsState = StatisticsState.empty()

    override fun subscriptions(): List<Sub<StatisticsState, StatisticsMsg>> = listOf(youngestOldestSubscription)

    override fun update(msg: StatisticsMsg, prevState: StatisticsState): Pair<StatisticsState, StatisticsCmd?> =
            when (msg) {
                is StatisticsFilterSelected -> onFilterSelected(prevState, msg)
                is YoungestOldestLoaded -> onOldestYoungestLoaded(prevState, msg)
                DoNothing -> prevState.noCmd()
            }

    private fun onOldestYoungestLoaded(prevState: StatisticsState, msg: YoungestOldestLoaded): Pair<StatisticsState, StatisticsCmd?> {
        val filter = prevState.filter
        val youngest = msg.youngest
        val oldest = msg.oldest

        val dateRanges = getDateRanges(filter, youngest, oldest)

        return prevState.copy(youngest = youngest, oldest = oldest,
                dateRanges = dateRanges).noCmd()
    }

    private fun onFilterSelected(prevState: StatisticsState, msg: StatisticsFilterSelected): Pair<StatisticsState, StatisticsCmd?> {
        val filter = msg.filter
        val youngest = prevState.youngest
        val oldest = prevState.oldest

        val dateRanges = getDateRanges(filter, youngest, oldest)

        return prevState.copy(filter = filter, dateRanges = dateRanges).noCmd()
    }

    private fun getDateRanges(statisticFilter: StatisticsFilter,
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

        return when (statisticFilter) {
            StatisticsFilter.OVERALL -> getOverallDateRange()
            StatisticsFilter.WEEK -> getWeekDateRanges(startDate, endDate)
            StatisticsFilter.MONTH -> TODO()
            StatisticsFilter.YEAR -> TODO()
        }
    }

    // Dummy date range for overall filter, needs at least one entry to populate view pager.
    private fun getOverallDateRange(): List<DateRangePair> {
        val emptyDateRange = DateRange.empty()
        return listOf(DateRangePair(emptyDateRange, emptyDateRange))
    }

    private fun getWeekDateRanges(startDate: LocalDate,
                                  endDate: LocalDate): List<DateRangePair> {
        val dateRangePairList = mutableListOf<DateRangePair>()
        var date = startDate
        val endDateNextWeek = endDate.plusWeeks(1)
        var isAtEnd: Boolean

        do {
            val monday = date.with(DayOfWeek.MONDAY)
            val sunday = date.with(DayOfWeek.SUNDAY)
            val previousMonday = monday.minusWeeks(1)
            val previousSunday = sunday.minusWeeks(1)

            val first = DateRange(monday, sunday)
            val second = DateRange(previousMonday, previousSunday)
            val pair = Pair(first, second)

            dateRangePairList.add(pair)

            date = date.plusWeeks(1)

            // Iterate until reaching week after end date.
            isAtEnd = date.year == endDateNextWeek.year &&
                    date.weekOfWeekBasedYear == endDateNextWeek.weekOfWeekBasedYear
        } while (!isAtEnd)

        return dateRangePairList
    }
}

// Subscription
class YoungestOldestSubscription @Inject constructor(private val statisticsDataSource: StatisticsDataSource) : StatelessSub<StatisticsState, StatisticsMsg>() {
    override fun invoke(): Observable<StatisticsMsg> =
            statisticsDataSource.getYoungestSleep()
                    .zipWith(statisticsDataSource.getOldestSleep())
                    .map { YoungestOldestLoaded(it.first, it.second) }
}

// State
data class StatisticsState(val filter: StatisticsFilter,
                           val youngest: Sleep,
                           val oldest: Sleep,
                           val dateRanges: List<DateRangePair>) : State {

    companion object {
        fun empty() = StatisticsState(StatisticsFilter.OVERALL, Sleep.empty(), Sleep.empty(), listOf())
    }
}

// Msg
sealed class StatisticsMsg : Msg

data class StatisticsFilterSelected(val filter: StatisticsFilter) : StatisticsMsg()

data class YoungestOldestLoaded(val youngest: Sleep, val oldest: Sleep) : StatisticsMsg()

object DoNothing : StatisticsMsg()

// Cmd
sealed class StatisticsCmd : Cmd