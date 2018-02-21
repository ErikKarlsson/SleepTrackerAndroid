package net.erikkarlsson.simplesleeptracker.domain

import io.reactivex.Single

interface SleepDataSource {
    fun getCurrent(): Single<Sleep>
    fun insert(newSleep: Sleep): Long
    fun update(updatedSleep: Sleep): Int
}