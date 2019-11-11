package net.erikkarlsson.simplesleeptracker.domain

import androidx.paging.PagedList
import com.google.common.collect.ImmutableList
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single

interface SleepDataSource {
    fun getCount(): Observable<Int>
    fun getSleep(): Observable<ImmutableList<net.erikkarlsson.simplesleeptracker.domain.entity.Sleep>>
    fun getSleepPaged(): Observable<PagedList<net.erikkarlsson.simplesleeptracker.domain.entity.Sleep>>
    fun getSleep(id: Int): Observable<net.erikkarlsson.simplesleeptracker.domain.entity.Sleep>
    fun getCurrent(): Observable<net.erikkarlsson.simplesleeptracker.domain.entity.Sleep>
    fun getCurrentSingle(): Single<net.erikkarlsson.simplesleeptracker.domain.entity.Sleep>
    fun insert(newSleep: net.erikkarlsson.simplesleeptracker.domain.entity.Sleep): Long
    fun update(updatedSleep: net.erikkarlsson.simplesleeptracker.domain.entity.Sleep): Int
    fun delete(sleep: net.erikkarlsson.simplesleeptracker.domain.entity.Sleep)
    fun deleteAll()
    fun insertAll(sleepList: ImmutableList<net.erikkarlsson.simplesleeptracker.domain.entity.Sleep>): Completable
}
