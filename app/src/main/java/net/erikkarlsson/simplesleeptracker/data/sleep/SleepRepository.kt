package net.erikkarlsson.simplesleeptracker.data.sleep

import androidx.paging.PagedList
import androidx.paging.RxPagedListBuilder
import com.google.common.collect.ImmutableList
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import net.erikkarlsson.simplesleeptracker.data.entity.SleepEntity
import net.erikkarlsson.simplesleeptracker.domain.SleepDataSource
import net.erikkarlsson.simplesleeptracker.domain.entity.Sleep
import net.erikkarlsson.simplesleeptracker.util.toImmutableList
import javax.inject.Inject

class SleepRepository @Inject constructor(private val sleepDao: SleepDao,
                                          private val sleepMapper: SleepMapper) : SleepDataSource {
    override fun getCount(): Observable<Int> =
            sleepDao.getSleepCount().toObservable()

    override fun getSleep(): Observable<ImmutableList<Sleep>> =
            sleepDao.getSleep()
                    .toObservable()
                    .switchMap { sleepList ->
                        Observable.fromIterable(sleepList)
                                .map { sleepMapper.mapFromEntity(it) }
                                .toImmutableList()
                                .toObservable()
                                .subscribeOn(Schedulers.computation())
                    }

    override fun getSleepPaged(): Observable<PagedList<Sleep>> =
            RxPagedListBuilder(
                    sleepDao.getSleepFactory().map { sleepMapper.mapFromEntity(it) },
                    50)
                    .buildObservable()

    override fun getSleep(id: Int): Observable<Sleep> =
            sleepDao.getSleep(id)
                    .map {
                        if (it.isEmpty()) Sleep.empty()
                        else sleepMapper.mapFromEntity(it[0])
                    }
                    .toObservable()
                    .onErrorReturnItem(Sleep.empty())

    override fun getCurrentSingle(): Single<Sleep> =
            sleepDao.getCurrentSleepSingle()
                    .map { sleepMapper.mapFromEntity(it) }
                    .onErrorReturnItem(Sleep.empty())

    override fun getCurrent(): Observable<Sleep> =
            sleepDao.getCurrentSleep()
                    .map {
                        if (it.isEmpty()) Sleep.empty()
                        else sleepMapper.mapFromEntity(it[0])
                    }
                    .toObservable()

    override fun insertAll(sleepList: ImmutableList<Sleep>): Completable {
        val sleepListBuilder = ImmutableList.Builder<SleepEntity>()
        for (sleep in sleepList) {
            sleepListBuilder.add(sleepMapper.mapToEntity(sleep))
        }
        return Completable.fromCallable { sleepDao.insertAll(sleepListBuilder.build()) }
    }

    override fun insert(newSleep: Sleep): Long {
        val sleepEntity = sleepMapper.mapToEntity(newSleep)
        return sleepDao.insertSleep(sleepEntity)
    }

    override fun update(updatedSleep: Sleep): Int {
        val sleepEntity = sleepMapper.mapToEntity(updatedSleep)
        return sleepDao.updateSleep(sleepEntity)
    }

    override fun delete(sleep: Sleep) =
            sleepDao.deleteSleep(sleepMapper.mapToEntity(sleep))

    override fun deleteAll() =
            sleepDao.deleteAllSleep()
}
