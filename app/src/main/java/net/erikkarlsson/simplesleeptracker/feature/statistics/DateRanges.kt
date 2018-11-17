package net.erikkarlsson.simplesleeptracker.feature.statistics

import net.erikkarlsson.simplesleeptracker.domain.entity.DateRange
import net.erikkarlsson.simplesleeptracker.util.weekOfWeekBasedYear
import org.threeten.bp.DayOfWeek
import org.threeten.bp.LocalDate

object DateRanges {

    // Dummy date range for overall filter, needs at least one entry to populate view pager.
    fun getOverallDateRange(): List<DateRangePair> {
        val emptyDateRange = DateRange.empty()
        return listOf(DateRangePair(emptyDateRange, emptyDateRange))
    }

    fun getWeekDateRanges(startDate: LocalDate,
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

    fun getMonthDateRanges(startDate: LocalDate, endDate: LocalDate): List<DateRangePair> {
        val dateRangePairList = mutableListOf<DateRangePair>()
        var date = startDate
        val endDateNextMonth = endDate.plusMonths(1)
        var isAtEnd: Boolean

        do {
            val firstDay = date.withDayOfMonth(1)
            val lastDay = date.withDayOfMonth(date.lengthOfMonth())
            val previousFirstDay = firstDay.minusMonths(1)
            val previousLastDay = previousFirstDay.withDayOfMonth(previousFirstDay.lengthOfMonth())

            val first = DateRange(firstDay, lastDay)
            val second = DateRange(previousFirstDay, previousLastDay)
            val pair = Pair(first, second)

            dateRangePairList.add(pair)

            date = date.plusMonths(1)

            // Iterate until reaching month after end month.
            isAtEnd = date.year == endDateNextMonth.year &&
                    date.month == endDateNextMonth.month
        } while (!isAtEnd)

        return dateRangePairList
    }

    fun getYearDateRanges(startDate: LocalDate, endDate: LocalDate): List<DateRangePair> {
        val dateRangePairList = mutableListOf<DateRangePair>()
        var date = startDate
        val endDateNextYear = endDate.plusYears(1)
        var isAtEnd: Boolean

        do {
            val firstDay = date.withDayOfYear(1)
            val lastDay = date.withDayOfYear(date.lengthOfYear())
            val previousFirstDay = firstDay.minusYears(1)
            val previousLastDay = previousFirstDay.withDayOfYear(previousFirstDay.lengthOfYear())

            val first = DateRange(firstDay, lastDay)
            val second = DateRange(previousFirstDay, previousLastDay)
            val pair = Pair(first, second)

            dateRangePairList.add(pair)

            date = date.plusYears(1)

            // Iterate until reaching month after end month.
            isAtEnd = date.year == endDateNextYear.year
        } while (!isAtEnd)

        return dateRangePairList
    }
}
