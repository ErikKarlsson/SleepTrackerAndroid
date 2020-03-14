package net.erikkarlsson.simplesleeptracker.domain

import androidx.paging.PagedList
import kotlinx.coroutines.flow.Flow
import net.erikkarlsson.simplesleeptracker.domain.entity.Sleep

interface SleepDataSourceCoroutines {
    fun getCount(): Flow<Int>
    fun getSleepPaged(): Flow<PagedList<Sleep>>
    fun getCurrentFlow(): Flow<Sleep>
    suspend fun getSleep(id: Int): Flow<Sleep>
    suspend fun getCurrent(): Sleep
    suspend fun update(updatedSleep: Sleep): Int
    suspend fun delete(sleep: Sleep)
    suspend fun insert(newSleep: Sleep): Long
}
