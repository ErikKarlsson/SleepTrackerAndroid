package net.erikkarlsson.simplesleeptracker.data

import io.reactivex.Observable
import io.reactivex.functions.Function7
import net.erikkarlsson.simplesleeptracker.domain.DayOfWeekLocalTime
import net.erikkarlsson.simplesleeptracker.domain.Statistics
import net.erikkarlsson.simplesleeptracker.domain.StatisticsDataSource
import net.erikkarlsson.simplesleeptracker.util.toLocalTime
import javax.inject.Inject

class StatisticsRepository @Inject constructor(private val sleepDao: SleepDao,
                                               private val sleepMapper: SleepMapper)
    : StatisticsDataSource {

    override fun getStatistics(): Observable<Statistics> {
        return Observable.zip(sleepDao.getSleepCount().toObservable(),
                sleepDao.getAverageSleepInHours().toObservable(),
                sleepDao.getLongestSleep().map { sleepMapper.mapFromEntity(it) }.toObservable(),
                sleepDao.getShortestSleep().map { sleepMapper.mapFromEntity(it) }.toObservable(),
                sleepDao.getAverageWakeupMidnightOffsetInSeconds().toObservable().map { it.toLocalTime },
                sleepDao.getAverageBedtimeMidnightOffsetInSeconds().toObservable().map { it.toLocalTime },
                sleepDao.getAverageBedtimeMidnightOffsetInSecondsForDaysOfWeek().toObservable().flatMap {
                    Observable.fromIterable(it).map {
                        DayOfWeekLocalTime(it.dayOfWeek, it.midnightOffsetInSeconds.toLocalTime)
                    }.toList().toObservable()
                },
                Function7 { sleepCount, averageSleep, longestNight, shortestSleep, averageWakeUpTime, averageBedTime, averageBedTimeDayOfWeek ->
                    Statistics(sleepCount, averageSleep, longestNight, shortestSleep, averageWakeUpTime, averageBedTime, averageBedTimeDayOfWeek)
                })
    }

}