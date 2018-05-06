package net.erikkarlsson.simplesleeptracker.domain

import com.google.common.collect.ImmutableList
import io.reactivex.Observable
import io.reactivex.Single
import net.erikkarlsson.simplesleeptracker.domain.entity.Sleep

interface SleepDataSource {
    fun getSleep(): Observable<ImmutableList<Sleep>>
    fun getSleep(id: Int): Observable<Sleep>
    fun getCurrent(): Observable<Sleep>
    fun getCurrentSingle(): Single<Sleep>
    fun insert(newSleep: Sleep): Long
    fun update(updatedSleep: Sleep): Int
    fun delete(sleep: Sleep)
    fun deleteAll()
}