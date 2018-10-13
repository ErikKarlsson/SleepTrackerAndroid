package net.erikkarlsson.simplesleeptracker.domain

import io.reactivex.Observable

interface PreferencesDataSource {
    fun set(key: String, value: Long)
    fun getLong(key: String): Observable<Long>
    fun clear()
}
