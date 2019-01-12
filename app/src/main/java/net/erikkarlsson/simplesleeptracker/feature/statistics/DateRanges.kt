package net.erikkarlsson.simplesleeptracker.feature.statistics

import net.erikkarlsson.simplesleeptracker.domain.entity.DateRange
import net.erikkarlsson.simplesleeptracker.util.weekOfWeekBasedYear
import org.threeten.bp.DayOfWeek

object DateRanges {

    // Dummy date range for overall filter, needs at least one entry to populate view pager.
    fun getOverallDateRange(): List<DateRangePair> {
        val emptyDateRange = DateRange.empty()
        return listOf(DateRangePair(emptyDateRange, emptyDateRange))
    }

    fun getWeekDateRanges(startEndDateRange: DateRange,
                          compareFilter: CompareFilter): List<DateRangePair> {
        val dateRangePairList = mutableListOf<DateRangePair>()
        var date = startEndDateRange.from
        val endMonday = startEndDateRange.to.plusWeeks(1).with(DayOfWeek.MONDAY)

        do {
            val monday = date.with(DayOfWeek.MONDAY)
            val sunday = date.with(DayOfWeek.SUNDAY)
            val previousMonday = monday.minusWeeks(1)
            val previousSunday = sunday.minusWeeks(1)
            val previousDateRange = DateRange(previousMonday, previousSunday)
            val first = DateRange(monday, sunday)
            val second = filterDateRange(previousDateRange, compareFilter)

            dateRangePairList.add(first to second)

            date = date.plusWeeks(1)

            // Iterate until reaching end week.
            val nextMonday = date.with(DayOfWeek.MONDAY)
            val isAtEnd = nextMonday.weekOfWeekBasedYear == endMonday.weekOfWeekBasedYear
                && nextMonday.year == endMonday.year

        } while (!isAtEnd)

        return dateRangePairList
    }

    fun getMonthDateRanges(startEndDateRange: DateRange,
                           compareFilter: CompareFilter): List<DateRangePair> {
        val dateRangePairList = mutableListOf<DateRangePair>()
        var date = startEndDateRange.from
        val endDateNextMonth = startEndDateRange.to.plusMonths(1)
        var isAtEnd: Boolean

        do {
            val firstDay = date.withDayOfMonth(1)
            val lastDay = date.withDayOfMonth(date.lengthOfMonth())
            val previousFirstDay = firstDay.minusMonths(1)
            val previousLastDay = previousFirstDay.withDayOfMonth(previousFirstDay.lengthOfMonth())
            val previousDateRange = DateRange(previousFirstDay, previousLastDay)
            val first = DateRange(firstDay, lastDay)
            val second = filterDateRange(previousDateRange, compareFilter)

            dateRangePairList.add(first to second)

            date = date.plusMonths(1)

            // Iterate until reaching month after end month.
            isAtEnd = date.year == endDateNextMonth.year &&
                    date.month == endDateNextMonth.month
        } while (!isAtEnd)

        return dateRangePairList
    }

    fun getYearDateRanges(startEndDateRange: DateRange,
                          compareFilter: CompareFilter): List<DateRangePair> {
        val dateRangePairList = mutableListOf<DateRangePair>()
        var date = startEndDateRange.from
        val endDateNextYear = startEndDateRange.to.plusYears(1)
        var isAtEnd: Boolean

        do {
            val firstDay = date.withDayOfYear(1)
            val lastDay = date.withDayOfYear(date.lengthOfYear())
            val previousFirstDay = firstDay.minusYears(1)
            val previousLastDay = previousFirstDay.withDayOfYear(previousFirstDay.lengthOfYear())
            val previousDateRange = DateRange(previousFirstDay, previousLastDay)
            val first = DateRange(firstDay, lastDay)
            val second = filterDateRange(previousDateRange, compareFilter)

            dateRangePairList.add(first to second)

            date = date.plusYears(1)

            // Iterate until reaching month after end month.
            isAtEnd = date.year == endDateNextYear.year
        } while (!isAtEnd)

        return dateRangePairList
    }

    private fun filterDateRange(previousDateRange: DateRange, compareFilter: CompareFilter) =
            when (compareFilter) {
                CompareFilter.PREVIOUS -> previousDateRange
                CompareFilter.OVERALL -> DateRange.infinite()
                CompareFilter.NONE -> DateRange.empty()
            }
}
