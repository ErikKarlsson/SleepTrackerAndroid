package net.erikkarlsson.simplesleeptracker.domain

import androidx.paging.PagedList
import com.google.common.collect.ImmutableList
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single
import kotlinx.coroutines.flow.Flow
import net.erikkarlsson.simplesleeptracker.domain.entity.Sleep

interface SleepDataSource {
    fun getCount(): Observable<Int>
    fun getCountFlow(): Flow<Int>
    fun getSleep(): Observable<ImmutableList<Sleep>>
    fun getSleepPaged(): Observable<PagedList<Sleep>>
    fun getSleepPagedFlow(): Flow<PagedList<Sleep>>
    fun getSleep(id: Int): Observable<Sleep>
    suspend fun getSleepCoroutine(id: Int): Flow<Sleep>
    fun getCurrent(): Observable<Sleep>
    fun getCurrentSingle(): Single<Sleep>
    suspend fun getCurrentCoroutines(): Sleep
    fun insert(newSleep: Sleep): Long
    fun update(updatedSleep: Sleep): Int
    suspend fun updateCoroutine(updatedSleep: Sleep): Int
    fun delete(sleep: Sleep)
    suspend fun deleteCoroutines(sleep: Sleep)
    fun deleteAll()
    fun insertAll(sleepList: ImmutableList<Sleep>): Completable
}
