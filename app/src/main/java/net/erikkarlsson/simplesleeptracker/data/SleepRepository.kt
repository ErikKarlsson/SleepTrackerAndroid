package net.erikkarlsson.simplesleeptracker.data

import io.reactivex.Flowable
import io.reactivex.Maybe
import javax.inject.Inject

class SleepRepository @Inject constructor(private val sleepDao: SleepDao)  {

    fun getCurrentSleep(): Maybe<Sleep> {
        return sleepDao.getCurrentSleep()
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
    /*
    fun getAverageSleepInHours(): Single<Int> {

    }

    fun insertSleep() {

    }
    */

}