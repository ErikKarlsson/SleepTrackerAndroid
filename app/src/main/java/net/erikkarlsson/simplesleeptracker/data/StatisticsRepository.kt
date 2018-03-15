package net.erikkarlsson.simplesleeptracker.data

import io.reactivex.Observable
import io.reactivex.functions.Function5
import net.erikkarlsson.simplesleeptracker.domain.Statistics
import net.erikkarlsson.simplesleeptracker.domain.StatisticsDataSource
import org.threeten.bp.LocalTime
import javax.inject.Inject

class StatisticsRepository @Inject constructor(private val sleepDao: SleepDao,
                                               private val sleepMapper: SleepMapper) :
        StatisticsDataSource {

    override fun getStatistics(): Observable<Statistics> {
        return Observable.zip(sleepDao.getAverageSleepInHours().toObservable(),
                sleepDao.getLongestSleep().map { sleepMapper.mapFromEntity(it) }.toObservable(),
                sleepDao.getShortestSleep().map { sleepMapper.mapFromEntity(it) }.toObservable(),
                sleepDao.getAverageWakeupMidnightOffsetInSeconds().toObservable().map { midnightOffsetToLocalTime(it) },
                sleepDao.getAverageBedtimeMidnightOffsetInSeconds().toObservable().map { midnightOffsetToLocalTime(it) },
                Function5 { averageSleep, longestNight, shortestSleep, averageWakeUpTime, averageBedTime ->
                    Statistics(averageSleep, longestNight, shortestSleep, averageWakeUpTime, averageBedTime)
                })

    }

    fun midnightOffsetToLocalTime(offsetSeconds: Int): LocalTime {
        val midnight = LocalTime.of(0, 0)
        return midnight.plusSeconds(offsetSeconds.toLong())
    }

}