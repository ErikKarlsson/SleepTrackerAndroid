package net.erikkarlsson.simplesleeptracker.domain

import io.reactivex.Observable
import io.reactivex.Single

interface SleepDataSource {
    fun getSleep(): Observable<List<Sleep>>
    fun getCurrent(): Single<Sleep>
    fun insert(newSleep: Sleep): Long
    fun update(updatedSleep: Sleep): Int
}