package net.erikkarlsson.simplesleeptracker.data

import io.reactivex.Observable
import io.reactivex.functions.Function8
import net.erikkarlsson.simplesleeptracker.domain.DayOfWeekLocalTime
import net.erikkarlsson.simplesleeptracker.domain.Statistics
import net.erikkarlsson.simplesleeptracker.domain.StatisticsDataSource
import net.erikkarlsson.simplesleeptracker.util.toLocalTime
import org.threeten.bp.LocalDate
import javax.inject.Inject

class StatisticsRepository @Inject constructor(private val sleepDao: SleepDao,
                                               private val sleepMapper: SleepMapper)
    : StatisticsDataSource {

    /**
     * Get statistics between dates
     */
    override fun getStatisticsBetweenDates(startDate: LocalDate, endDate: LocalDate): Observable<Statistics> {
        val from = startDate.toString()
        val to = endDate.toString()
        return Observable.zip(sleepDao.getSleepCount(from, to).toObservable(),
                sleepDao.getAverageSleepInHours(from, to).toObservable(),
                sleepDao.getLongestSleep(from, to).map { sleepMapper.mapFromEntity(it) }.toObservable(),
                sleepDao.getShortestSleep(from, to).map { sleepMapper.mapFromEntity(it) }.toObservable(),
                sleepDao.getAverageWakeupMidnightOffsetInSeconds(from, to).toObservable().map { it.toLocalTime },
                sleepDao.getAverageBedtimeMidnightOffsetInSeconds(from, to).toObservable().map { it.toLocalTime },
                sleepDao.getAverageBedtimeMidnightOffsetInSecondsForDaysOfWeek(from, to).toObservable().flatMap { toDayOfWeekLocalTime(it) },
                sleepDao.getAverageWakeupMidnightOffsetInSecondsForDaysOfWeek(from, to).toObservable().flatMap { toDayOfWeekLocalTime(it) },
                Function8 { sleepCount, averageSleep, longestNight, shortestSleep, averageWakeUpTime, averageBedTime, averageBedTimeDayOfWeek, averageWakeupTimeDayOfWeek ->
                    Statistics(sleepCount, averageSleep, longestNight, shortestSleep, averageWakeUpTime, averageBedTime, averageBedTimeDayOfWeek, averageWakeupTimeDayOfWeek)
                })
    }

    /**
     * Get overall statistics
     */
    override fun getStatistics(): Observable<Statistics> {
        val startDate = LocalDate.of(1000, 1, 1)
        val endDate = LocalDate.of(9999, 1, 1)
        return getStatisticsBetweenDates(startDate, endDate)
    }

    private fun toDayOfWeekLocalTime(dayOfWeekMidnightOffset: List<DayOfWeekMidnightOffset>): Observable<List<DayOfWeekLocalTime>> {
        return Observable.fromIterable(dayOfWeekMidnightOffset).map {
            DayOfWeekLocalTime(it.dayOfWeek, it.midnightOffsetInSeconds.toLocalTime)
        }.toList().toObservable()
    }

}