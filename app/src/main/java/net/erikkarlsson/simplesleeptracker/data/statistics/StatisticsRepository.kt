package net.erikkarlsson.simplesleeptracker.data.statistics

import com.google.common.collect.ImmutableList
import io.reactivex.Observable
import io.reactivex.rxkotlin.Observables
import io.reactivex.schedulers.Schedulers
import net.erikkarlsson.simplesleeptracker.data.entity.DayOfWeekHours
import net.erikkarlsson.simplesleeptracker.data.entity.DayOfWeekMidnightOffset
import net.erikkarlsson.simplesleeptracker.data.entity.SleepEntity
import net.erikkarlsson.simplesleeptracker.data.sleep.SleepMapper
import net.erikkarlsson.simplesleeptracker.domain.StatisticsDataSource
import net.erikkarlsson.simplesleeptracker.domain.entity.*
import net.erikkarlsson.simplesleeptracker.util.midnightOffsetToLocalTime
import net.erikkarlsson.simplesleeptracker.util.toImmutableList
import org.threeten.bp.LocalTime
import javax.inject.Inject

class StatisticsRepository @Inject constructor(private val statisticsDao: StatisticsDao,
                                               private val sleepMapper: SleepMapper)
    : StatisticsDataSource {

    override fun getYoungestSleep(): Observable<Sleep> =
            statisticsDao.getYoungestSleep()
                    .map(::firstOrEmpty)
                    .map(sleepMapper::mapFromEntity).toObservable()

    override fun getOldestSleep(): Observable<Sleep> =
            statisticsDao.getOldestSleep()
                    .map(::firstOrEmpty)
                    .map(sleepMapper::mapFromEntity).toObservable()

    override fun getStatistics(): Observable<Statistics> {
        val infiniteDateRange = DateRange.infinite()
        return getStatistics(infiniteDateRange)
    }

    override fun getStatistics(dateRange: DateRange): Observable<Statistics> {
        val from = dateRange.from.toString()
        val to = dateRange.to.toString()

        return getSleepCount(from, to)
                .switchMap { sleepCount ->
                    if (sleepCount == 0) {
                        Observable.just(Statistics.empty())
                    } else {
                        Observables.combineLatest(
                                getAverageSleepInHours(from, to),
                                getLongestSleep(from, to),
                                getShortestSleep(from, to),
                                getAverageWakeUpTime(from, to),
                                getAverageBedtime(from, to),
                                getAverageBedtimeDayOfWeek(from, to),
                                getAverageWakeUpTimeDayOfWeek(from, to),
                                getAverageSleepDurationDayOfWeek(from, to))
                        { averageSleep, longestNight, shortestSleep, averageWakeUpTime, averageBedTime, averageBedTimeDayOfWeek, averageWakeupTimeDayOfWeek, averageSleepDurationDayOfWeek ->
                            Statistics(sleepCount, averageSleep, longestNight, shortestSleep, averageWakeUpTime, averageBedTime, averageBedTimeDayOfWeek, averageWakeupTimeDayOfWeek, averageSleepDurationDayOfWeek)
                        }
                                .subscribeOn(Schedulers.io())
                    }
                }
    }

    override fun getSleepCountYearMonth(): Observable<List<SleepCountYearMonth>> =
            statisticsDao.getCountGroupedByYearMonth().toObservable()

    private fun firstOrEmpty(sleepList: List<SleepEntity>): SleepEntity =
            sleepList.firstOrNull() ?: SleepEntity.empty()

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
