package net.erikkarlsson.simplesleeptracker.data.statistics

import com.google.common.collect.ImmutableList
import io.reactivex.Observable
import io.reactivex.rxkotlin.Observables
import net.erikkarlsson.simplesleeptracker.data.DayOfWeekHours
import net.erikkarlsson.simplesleeptracker.data.DayOfWeekMidnightOffset
import net.erikkarlsson.simplesleeptracker.data.sleep.SleepMapper
import net.erikkarlsson.simplesleeptracker.domain.StatisticsDataSource
import net.erikkarlsson.simplesleeptracker.domain.entity.DateRange
import net.erikkarlsson.simplesleeptracker.domain.entity.DayOfWeekLocalTime
import net.erikkarlsson.simplesleeptracker.domain.entity.Sleep
import net.erikkarlsson.simplesleeptracker.domain.entity.SleepCountYearMonth
import net.erikkarlsson.simplesleeptracker.domain.entity.Statistics
import net.erikkarlsson.simplesleeptracker.util.midnightOffsetToLocalTime
import net.erikkarlsson.simplesleeptracker.util.toImmutableList
import org.threeten.bp.LocalDate
import org.threeten.bp.LocalTime
import javax.inject.Inject

class StatisticsRepository @Inject constructor(private val statisticsDao: StatisticsDao,
                                               private val sleepMapper: SleepMapper)
    : StatisticsDataSource {

    override fun getStatistics(): Observable<Statistics> {
        val infiniteDateRange = DateRange(LocalDate.parse("1000-01-01"), LocalDate.parse("9999-01-01"))
        return getStatistics(infiniteDateRange)
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
                getAverageWakeUpTimeDayOfWeek(from, to),
                getAverageSleepDurationDayOfWeek(from, to))
                { sleepCount, averageSleep, longestNight, shortestSleep, averageWakeUpTime, averageBedTime, averageBedTimeDayOfWeek, averageWakeupTimeDayOfWeek, averageSleepDurationDayOfWeek ->
                    Statistics(sleepCount, averageSleep, longestNight, shortestSleep, averageWakeUpTime, averageBedTime, averageBedTimeDayOfWeek, averageWakeupTimeDayOfWeek, averageSleepDurationDayOfWeek)
                }
    }

    override fun getSleepCountYearMonth(): Observable<List<SleepCountYearMonth>> =
        statisticsDao.getCountGroupedByYearMonth().toObservable()

    private fun getSleepCount(from: String, to: String): Observable<Int> =
            statisticsDao.getSleepCount(from, to).toObservable()

    private fun getAverageSleepInHours(from: String, to: String): Observable<Float> =
            statisticsDao.getAverageSleepInHours(from, to).toObservable()

    private fun getAverageSleepDurationDayOfWeek(from: String, to: String): Observable<ImmutableList<DayOfWeekHours>> =
            statisticsDao.getAverageSleepDurationDayOfWeek(from, to).toObservable().map { ImmutableList.copyOf(it) }

    private fun getLongestSleep(from: String, to: String): Observable<Sleep> =
            statisticsDao.getLongestSleep(from, to).map { sleepMapper.mapFromEntity(it) }.toObservable()

    private fun getShortestSleep(from: String, to: String): Observable<Sleep> =
            statisticsDao.getShortestSleep(from, to).map { sleepMapper.mapFromEntity(it) }.toObservable()

    private fun getAverageWakeUpTime(from: String, to: String): Observable<LocalTime> =
            statisticsDao.getAverageWakeUpMidnightOffsetInSeconds(from, to).toObservable().map { it.midnightOffsetToLocalTime }

    private fun getAverageBedtime(from: String, to: String): Observable<LocalTime> =
            statisticsDao.getAverageBedtimeMidnightOffsetInSeconds(from, to).toObservable().map { it.midnightOffsetToLocalTime }

    private fun getAverageBedtimeDayOfWeek(from: String, to: String): Observable<ImmutableList<DayOfWeekLocalTime>> =
            statisticsDao.getAverageBedtimeMidnightOffsetInSecondsForDaysOfWeek(from, to).toObservable().flatMap { toDayOfWeekLocalTimeListObservable(it) }

    private fun getAverageWakeUpTimeDayOfWeek(from: String, to: String): Observable<ImmutableList<DayOfWeekLocalTime>> =
            statisticsDao.getAverageWakeUpMidnightOffsetInSecondsForDaysOfWeek(from, to).toObservable().flatMap { toDayOfWeekLocalTimeListObservable(it) }

    private fun toDayOfWeekLocalTimeListObservable(dayOfWeekMidnightOffset: List<DayOfWeekMidnightOffset>)
            : Observable<ImmutableList<DayOfWeekLocalTime>> =
            Observable.fromIterable(dayOfWeekMidnightOffset).map { it.toDayOfWeekLocalTime }.toImmutableList().toObservable()

}