package net.erikkarlsson.simplesleeptracker.domain

import io.reactivex.Completable
import io.reactivex.Observable

interface PreferencesDataSource {
    fun set(key: String, value: Long)
    fun set(key: String, value: Int)
    fun set(key: String, value: Boolean): Completable
    fun set(key: String, value: String): Completable
    fun getLong(key: String): Observable<Long>
    fun getInt(key: String): Observable<Int>
    fun getBoolean(key: String): Observable<Boolean>
    fun getString(key: String): Observable<String>
    fun clear()
}
