package net.erikkarlsson.simplesleeptracker.data

import io.reactivex.Flowable
import javax.inject.Inject

class SleepRepository @Inject constructor(private val sleepDao: SleepDao)  {

    fun getSleep(): Flowable<List<Sleep>> {
        return sleepDao.getSleep()
    }
    /*
    fun getAverageSleepInHours(): Single<Int> {

    }

    fun insertSleep() {

    }
    */

}