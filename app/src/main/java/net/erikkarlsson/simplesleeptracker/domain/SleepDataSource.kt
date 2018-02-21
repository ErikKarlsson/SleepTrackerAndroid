package net.erikkarlsson.simplesleeptracker.domain

import io.reactivex.Single

interface SleepDataSource {
    fun getCurrentSleep(): Single<Sleep>
    fun insertSleep(newSleep: Sleep): Long
    fun updateSleep(updatedSleep: Sleep): Int
}