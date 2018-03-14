package net.erikkarlsson.simplesleeptracker.data

import io.reactivex.Single
import io.reactivex.functions.Function5
import net.erikkarlsson.simplesleeptracker.domain.Statistics
import net.erikkarlsson.simplesleeptracker.domain.StatisticsDataSource
import org.threeten.bp.LocalTime
import javax.inject.Inject

class StatisticsRepository @Inject constructor(private val sleepDao: SleepDao) :
        StatisticsDataSource {

    override fun getStatistics(): Single<Statistics> {
        return Single.zip(sleepDao.getAverageSleepInHours(),
                sleepDao.getLongestSleepInHours(),
                sleepDao.getShortestSleepInHours(),
                sleepDao.getAverageWakeupMidnightOffsetInSeconds().map { midnightOffsetToLocalTime(it) },
                sleepDao.getAverageBedtimeMidnightOffsetInSeconds().map { midnightOffsetToLocalTime(it) },
                Function5 { averageSleep, longestNight, shortestSleep, averageWakeUpTime, averageBedTime ->
                    Statistics(averageSleep, longestNight, shortestSleep, averageWakeUpTime, averageBedTime)
                })
    }

    fun midnightOffsetToLocalTime(offsetSeconds: Int): LocalTime {
        val midnight = LocalTime.of(0, 0)
        return midnight.plusSeconds(offsetSeconds.toLong())
    }

}