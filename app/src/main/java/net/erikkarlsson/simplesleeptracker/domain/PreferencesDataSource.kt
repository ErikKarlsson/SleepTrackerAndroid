package net.erikkarlsson.simplesleeptracker.domain

import io.reactivex.Observable

interface PreferencesDataSource {
    fun set(key: String, value: Long)
    fun set(key: String, value: Int)
    fun getLong(key: String): Observable<Long>
    fun getInt(key: String): Observable<Int>
    fun clear()
}
