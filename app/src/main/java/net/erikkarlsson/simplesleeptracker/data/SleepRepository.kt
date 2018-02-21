package net.erikkarlsson.simplesleeptracker.data

import io.reactivex.Flowable
import io.reactivex.Single
import javax.inject.Inject

class SleepRepository @Inject constructor(private val sleepDao: SleepDao)  {

    fun getCurrentSleep(): Single<Sleep> {
        return sleepDao.getCurrentSleep().toSingle().onErrorReturnItem(Sleep.empty())
    }

    fun getSleep(): Flowable<List<Sleep>> {
        return sleepDao.getSleep()
    }

    fun insertSleep(newSleep: Sleep): Long {
        return sleepDao.insertSleep(newSleep)
    }

    fun updateSleep(updatedSleep: Sleep): Int {
        return sleepDao.updateSleep(updatedSleep)
    }

}