package net.erikkarlsson.simplesleeptracker.domain.entity

import com.google.common.collect.ImmutableList
import net.erikkarlsson.simplesleeptracker.dateutil.hoursTo
import org.threeten.bp.DayOfWeek
import org.threeten.bp.LocalTime
import org.threeten.bp.OffsetDateTime

data class StatisticComparison(val first: Statistics, val second: Statistics) {

    val avgSleepDiffHours: Float get() = if (second.isEmpty) 0.0f else first.avgSleepHours - second.avgSleepHours
    val avgBedTimeDiffHours: Float get() = if (second.isEmpty) 0.0f else second.averageBedTime.hoursTo(first.averageBedTime)
    val avgWakeUpTimeDiffHours: Float get() = if (second.isEmpty) 0.0f else second.averageWakeUpTime.hoursTo(first.averageWakeUpTime)

    companion object {
        fun empty() = StatisticComparison(Statistics.empty(), Statistics.empty())

        fun demo(): StatisticComparison {
            val now = OffsetDateTime.now()
            val yesterday = now.minusDays(1)
            val aFewDaysAgo = now.minusDays(12)
            val longestSleep = Sleep(fromDate = yesterday, toDate = yesterday.plusHours(8).plusMinutes(45))
            val shortestSleep = Sleep(fromDate = aFewDaysAgo, toDate = aFewDaysAgo.plusHours(6).plusMinutes(35))
            val averageWakeUpTime = LocalTime.of(6, 30)
            val averageBedTime = LocalTime.of(23, 0)
            val averageSleepDurationDayOfWeek = ImmutableList.of(
                    DayOfWeekHours(DayOfWeek.MONDAY.value, 8.1f),
                    DayOfWeekHours(DayOfWeek.TUESDAY.value, 8.75f),
                    DayOfWeekHours(DayOfWeek.WEDNESDAY.value, 7f),
                    DayOfWeekHours(DayOfWeek.THURSDAY.value, 6.583f),
                    DayOfWeekHours(DayOfWeek.FRIDAY.value, 7.6f),
                    DayOfWeekHours(DayOfWeek.SATURDAY.value, 7.9f),
                    DayOfWeekHours(DayOfWeek.SUNDAY.value, 8f)
            )
            val averageBedTimeDayOfWeek = ImmutableList.of(
                    DayOfWeekLocalTime(DayOfWeek.MONDAY, LocalTime.of(22, 10)),
                    DayOfWeekLocalTime(DayOfWeek.TUESDAY, LocalTime.of(22, 15)),
                    DayOfWeekLocalTime(DayOfWeek.WEDNESDAY, LocalTime.of(21, 14)),
                    DayOfWeekLocalTime(DayOfWeek.THURSDAY, LocalTime.of(23, 10)),
                    DayOfWeekLocalTime(DayOfWeek.FRIDAY, LocalTime.of(23, 30)),
                    DayOfWeekLocalTime(DayOfWeek.SATURDAY, LocalTime.of(23, 23)),
                    DayOfWeekLocalTime(DayOfWeek.SUNDAY, LocalTime.of(23, 10))
            )
            val averageWakeUpTimeDayOfWeek = ImmutableList.of(
                    DayOfWeekLocalTime(DayOfWeek.MONDAY, LocalTime.of(6, 10)),
                    DayOfWeekLocalTime(DayOfWeek.TUESDAY, LocalTime.of(6, 20)),
                    DayOfWeekLocalTime(DayOfWeek.WEDNESDAY, LocalTime.of(5, 30)),
                    DayOfWeekLocalTime(DayOfWeek.THURSDAY, LocalTime.of(5, 50)),
                    DayOfWeekLocalTime(DayOfWeek.FRIDAY, LocalTime.of(6, 10)),
                    DayOfWeekLocalTime(DayOfWeek.SATURDAY, LocalTime.of(7, 23)),
                    DayOfWeekLocalTime(DayOfWeek.SUNDAY, LocalTime.of(7, 40))
            )
            val statistics = Statistics(10, 7.7f, longestSleep, shortestSleep,
                    averageWakeUpTime, averageBedTime, averageBedTimeDayOfWeek,
                    averageWakeUpTimeDayOfWeek, averageSleepDurationDayOfWeek)

            return StatisticComparison(statistics, Statistics.empty())
        }
    }

}
