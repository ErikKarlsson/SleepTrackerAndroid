package net.erikkarlsson.simplesleeptracker.domain

import kotlinx.coroutines.flow.Flow

interface PreferencesDataSourceFlow {
    suspend fun set(key: String, value: Long)
    suspend fun set(key: String, value: Int)
    suspend fun set(key: String, value: Boolean)
    fun getLong(key: String): Flow<Long>
    fun getInt(key: String): Flow<Int>
    fun getBoolean(key: String): Flow<Boolean>
    suspend fun clear()
}
