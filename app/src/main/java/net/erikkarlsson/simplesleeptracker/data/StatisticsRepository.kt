package net.erikkarlsson.simplesleeptracker.data

import io.reactivex.Single
import net.erikkarlsson.simplesleeptracker.domain.Statistics
import net.erikkarlsson.simplesleeptracker.domain.StatisticsDataSource
import javax.inject.Inject

class StatisticsRepository @Inject constructor(private val sleepDao: SleepDao) :
        StatisticsDataSource {

    override fun getStatistics(): Single<Statistics> {
        return sleepDao.getAverageSleepInHours().map { Statistics(it) }
    }

}