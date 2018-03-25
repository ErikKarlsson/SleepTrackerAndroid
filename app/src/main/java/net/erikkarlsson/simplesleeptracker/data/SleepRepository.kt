package net.erikkarlsson.simplesleeptracker.data

import com.google.common.collect.ImmutableList
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import net.erikkarlsson.simplesleeptracker.domain.SleepDataSource
import net.erikkarlsson.simplesleeptracker.domain.entity.Sleep
import net.erikkarlsson.simplesleeptracker.util.toImmutableList
import javax.inject.Inject

class SleepRepository @Inject constructor(private val sleepDao: SleepDao,
                                          private val sleepMapper: SleepMapper) : SleepDataSource {
    override fun getSleep(): Observable<ImmutableList<Sleep>> {
        return sleepDao.getSleep().toObservable()
            .switchMap {
                Observable.fromIterable(it)
                    .map { sleepMapper.mapFromEntity(it) }
                    .toImmutableList().toObservable()
                    .subscribeOn(Schedulers.computation())
            }
    }

    override fun getCurrentSingle(): Single<Sleep> {
        return sleepDao.getCurrentSleepSingle()
            .map { sleepMapper.mapFromEntity(it) }
            .onErrorReturnItem(Sleep.empty())
    }

    override fun getCurrent(): Observable<Sleep> {
        return sleepDao.getCurrentSleep()
            .map {
                if (it.isEmpty()) Sleep.empty()
                else sleepMapper.mapFromEntity(it[0])
            }
            .toObservable()
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

    override fun delete(sleep: Sleep) {
        sleepDao.deleteSleep(sleepMapper.mapToEntity(sleep))
    }

    override fun deleteAll() {
        sleepDao.deleteAllSleep()
    }

}
