package net.erikkarlsson.simplesleeptracker.data

import com.google.common.collect.ImmutableList
import io.reactivex.Observable
import io.reactivex.rxkotlin.Observables
import net.erikkarlsson.simplesleeptracker.domain.StatisticsDataSource
import net.erikkarlsson.simplesleeptracker.domain.entity.DateRange
import net.erikkarlsson.simplesleeptracker.domain.entity.DayOfWeekLocalTime
import net.erikkarlsson.simplesleeptracker.domain.entity.Sleep
import net.erikkarlsson.simplesleeptracker.domain.entity.Statistics
import net.erikkarlsson.simplesleeptracker.util.localTime
import net.erikkarlsson.simplesleeptracker.util.toImmutableList
import org.threeten.bp.LocalDate
import org.threeten.bp.LocalTime
import javax.inject.Inject

class StatisticsRepository @Inject constructor(private val sleepDao: SleepDao,
                                               private val sleepMapper: SleepMapper)
    : StatisticsDataSource {

    override fun getStatistics(): Observable<Statistics> {
        return getStatistics(DateRange(LocalDate.parse("1000-01-01"), LocalDate.parse("9999-01-01"))) // Infinite date range
    }

    override fun getStatistics(dateRange: DateRange): Observable<Statistics> {
        val from = dateRange.from.toString()
        val to = dateRange.to.toString()

        return Observables.combineLatest(
                getSleepCount(from, to),
                getAverageSleepInHours(from, to),
                getLongestSleep(from, to),
                getShortestSleep(from, to),
                getAverageWakeUpTime(from, to),
                getAverageBedtime(from, to),
                getAverageBedtimeDayOfWeek(from, to),
                getAverageWakeUpTimeDayOfWeek(from, to))
                { sleepCount, averageSleep, longestNight, shortestSleep, averageWakeUpTime, averageBedTime, averageBedTimeDayOfWeek, averageWakeupTimeDayOfWeek ->
                    Statistics(sleepCount, averageSleep, longestNight, shortestSleep, averageWakeUpTime, averageBedTime, averageBedTimeDayOfWeek, averageWakeupTimeDayOfWeek)
                }
    }

    private fun getSleepCount(from: String, to: String): Observable<Int> =
            sleepDao.getSleepCount(from, to).toObservable()

    private fun getAverageSleepInHours(from: String, to: String): Observable<Float> =
            sleepDao.getAverageSleepInHours(from, to).toObservable()

    private fun getLongestSleep(from: String, to: String): Observable<Sleep> =
            sleepDao.getLongestSleep(from, to).map { sleepMapper.mapFromEntity(it) }.toObservable()

    private fun getShortestSleep(from: String, to: String): Observable<Sleep> =
            sleepDao.getShortestSleep(from, to).map { sleepMapper.mapFromEntity(it) }.toObservable()

    private fun getAverageWakeUpTime(from: String, to: String): Observable<LocalTime> =
            sleepDao.getAverageWakeUpMidnightOffsetInSeconds(from, to).toObservable().map { it.localTime }

    private fun getAverageBedtime(from: String, to: String): Observable<LocalTime> =
            sleepDao.getAverageBedtimeMidnightOffsetInSeconds(from, to).toObservable().map { it.localTime }

    private fun getAverageBedtimeDayOfWeek(from: String, to: String): Observable<ImmutableList<DayOfWeekLocalTime>> =
            sleepDao.getAverageBedtimeMidnightOffsetInSecondsForDaysOfWeek(from, to).toObservable().flatMap { toDayOfWeekLocalTimeListObservable(it) }

    private fun getAverageWakeUpTimeDayOfWeek(from: String, to: String): Observable<ImmutableList<DayOfWeekLocalTime>> =
            sleepDao.getAverageWakeUpMidnightOffsetInSecondsForDaysOfWeek(from, to).toObservable().flatMap { toDayOfWeekLocalTimeListObservable(it) }

    private fun toDayOfWeekLocalTimeListObservable(dayOfWeekMidnightOffset: List<DayOfWeekMidnightOffset>)
            : Observable<ImmutableList<DayOfWeekLocalTime>> =
            Observable.fromIterable(dayOfWeekMidnightOffset).map { it.toDayOfWeekLocalTime }.toImmutableList().toObservable()

}