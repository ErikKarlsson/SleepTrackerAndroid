package net.erikkarlsson.simplesleeptracker.data

import io.reactivex.Single
import net.erikkarlsson.simplesleeptracker.domain.Sleep
import net.erikkarlsson.simplesleeptracker.domain.SleepDataSource
import javax.inject.Inject

class SleepRepository @Inject constructor(private val sleepDao: SleepDao,
                                          private val sleepMapper: SleepMapper) : SleepDataSource {
    override fun getCurrent(): Single<Sleep> {
        return sleepDao.getCurrentSleep()
            .map { sleepMapper.mapFromEntity(it) }
            .toSingle()
            .onErrorReturnItem(Sleep.empty())
    }

    override fun insert(newSleep: Sleep): Long {
        val sleepEntity = sleepMapper.mapToEntity(newSleep)
        return sleepDao.insertSleep(sleepEntity)
    }

    override fun update(updatedSleep: Sleep): Int {
        val sleepEntity = sleepMapper.mapToEntity(updatedSleep)
        return sleepDao.updateSleep(sleepEntity)
    }

}