package net.erikkarlsson.simplesleeptracker.domain.entity

import com.google.common.collect.ImmutableList
import net.erikkarlsson.simplesleeptracker.data.DayOfWeekHours
import org.threeten.bp.LocalTime

data class Statistics(val sleepCount: Int,
                      val avgSleepHours: Float,
                      val longestSleep: Sleep,
                      val shortestSleep: Sleep,
                      val averageWakeUpTime: LocalTime,
                      val averageBedTime: LocalTime,
                      val averageBedTimeDayOfWeek: ImmutableList<DayOfWeekLocalTime>,
                      val averageWakeUpTimeDayOfWeek: ImmutableList<DayOfWeekLocalTime>,
                      val averageSleepDurationDayOfWeek: ImmutableList<DayOfWeekHours>) {

    val timeSleepingPercentage get(): Int = Math.min(Math.round(avgSleepHours / 24 * 100), 100)

    val isEmpty get(): Boolean = this == empty()

    fun averageSleepDurationDayOfWeekFor(day: Int): DayOfWeekHours? {
        for (dayOfWeekHours in averageSleepDurationDayOfWeek) {
            if (dayOfWeekHours.day == day) {
                return dayOfWeekHours
            }
        }
        return null
    }

    val longestSleepDurationDayOfWeekInHours: Float
        get() {
            var longest = 0f

            for (dayOfWeekHours in averageSleepDurationDayOfWeek) {
                if (dayOfWeekHours.hours > longest) {
                    longest = dayOfWeekHours.hours
                }
            }

            return longest
        }

    companion object {
        fun empty() = Statistics(0, 0.0f, Sleep.empty(), Sleep.empty(), LocalTime.MAX, LocalTime.MAX, ImmutableList.of(), ImmutableList.of(), ImmutableList.of())
    }
}
