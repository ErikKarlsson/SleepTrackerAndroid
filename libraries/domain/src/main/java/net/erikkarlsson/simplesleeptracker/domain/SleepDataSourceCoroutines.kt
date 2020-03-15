package net.erikkarlsson.simplesleeptracker.domain

import androidx.paging.PagedList
import com.google.common.collect.ImmutableList
import kotlinx.coroutines.flow.Flow
import net.erikkarlsson.simplesleeptracker.domain.entity.Sleep

interface SleepDataSourceCoroutines {
    fun getCount(): Flow<Int>
    fun getSleepPaged(): Flow<PagedList<Sleep>>
    fun getSleepListFlow(): Flow<ImmutableList<Sleep>>
    fun getCurrentFlow(): Flow<Sleep>
    fun getSleepFlow(id: Int): Flow<Sleep>
    suspend fun getCurrent(): Sleep
    suspend fun update(updatedSleep: Sleep): Int
    suspend fun delete(sleep: Sleep)
    suspend fun deleteAll()
    suspend fun insert(newSleep: Sleep): Long
}
