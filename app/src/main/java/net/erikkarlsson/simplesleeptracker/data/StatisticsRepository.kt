package net.erikkarlsson.simplesleeptracker.data

import io.reactivex.Observable
import io.reactivex.rxkotlin.Observables
import net.erikkarlsson.simplesleeptracker.domain.*
import net.erikkarlsson.simplesleeptracker.util.toLocalTime
import org.threeten.bp.DayOfWeek
import org.threeten.bp.LocalDate
import org.threeten.bp.LocalTime
import javax.inject.Inject

class StatisticsRepository @Inject constructor(private val sleepDao: SleepDao,
                                               private val sleepMapper: SleepMapper,
                                               private val dateTimeProvider: DateTimeProvider)
    : StatisticsDataSource {

    override fun getStatisticComparisonBetweenCurrentAndPreviousWeek(): Observable<StatisticComparison> {
        val now = dateTimeProvider.now().toLocalDate()
        val monday = now.with(DayOfWeek.MONDAY)
        val sunday = now.with(DayOfWeek.SUNDAY)
        val currentWeek = DateRange(monday, sunday)
        val previousWeek = DateRange(monday.minusWeeks(1), sunday.minusWeeks(1))

        return getStatisticComparisonBetween(currentWeek, previousWeek)
    }

    override fun getStatisticComparisonBetween(first: DateRange, second: DateRange): Observable<StatisticComparison> {
        return Observables.zip(
                getStatisticsBetween(first.from, first.to),
                getStatisticsBetween(second.from, second.to))
                { firstWeek, secondWeek -> StatisticComparison(firstWeek, secondWeek) }
    }

    override fun getStatisticsBetween(startDate: LocalDate, endDate: LocalDate): Observable<Statistics> {
        val from = startDate.toString()
        val to = endDate.toString()

        return Observables.zip(getSleepCount(from, to),
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
            sleepDao.getAverageWakeUpMidnightOffsetInSeconds(from, to).toObservable().map { it.toLocalTime }

    private fun getAverageBedtime(from: String, to: String): Observable<LocalTime> =
            sleepDao.getAverageBedtimeMidnightOffsetInSeconds(from, to).toObservable().map { it.toLocalTime }

    private fun getAverageBedtimeDayOfWeek(from: String, to: String): Observable<List<DayOfWeekLocalTime>> =
            sleepDao.getAverageBedtimeMidnightOffsetInSecondsForDaysOfWeek(from, to).toObservable().flatMap { toDayOfWeekLocalTimeListObservable(it) }

    private fun getAverageWakeUpTimeDayOfWeek(from: String, to: String): Observable<List<DayOfWeekLocalTime>> =
            sleepDao.getAverageWakeUpMidnightOffsetInSecondsForDaysOfWeek(from, to).toObservable().flatMap { toDayOfWeekLocalTimeListObservable(it) }

    private fun toDayOfWeekLocalTimeListObservable(dayOfWeekMidnightOffset: List<DayOfWeekMidnightOffset>)
            : Observable<List<DayOfWeekLocalTime>> =
            Observable.fromIterable(dayOfWeekMidnightOffset).map { it.dayOfWeekLocalTime }.toList().toObservable()

}