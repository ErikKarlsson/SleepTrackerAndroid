package net.erikkarlsson.simplesleeptracker.domain

import kotlinx.coroutines.flow.Flow

interface PreferencesDataSource {
    fun set(key: String, value: Long)
    fun set(key: String, value: Int)
    fun set(key: String, value: Boolean)
    fun getLong(key: String): Flow<Long>
    fun getInt(key: String): Flow<Int>
    fun getBoolean(key: String): Flow<Boolean>
    fun clear()
}
