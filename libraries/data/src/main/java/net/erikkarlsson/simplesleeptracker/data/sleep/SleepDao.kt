package net.erikkarlsson.simplesleeptracker.data.sleep

import androidx.paging.DataSource
import androidx.room.*
import kotlinx.coroutines.flow.Flow
import net.erikkarlsson.simplesleeptracker.data.entity.SleepEntity

/**
 * Data Access Object for the sleep table.
 */
@Dao
interface SleepDao {

    @Query("SELECT count(*) FROM Sleep")
    fun getSleepCountFlow(): Flow<Int>

    @Query("SELECT * FROM Sleep WHERE to_date != 0 ORDER BY datetime(from_date) DESC")
    fun getSleepFlow(): Flow<List<SleepEntity>>

    @Query("SELECT * FROM Sleep WHERE to_date != 0 ORDER BY datetime(from_date) DESC")
    fun getSleepFactory(): DataSource.Factory<Int, SleepEntity>

    @Query("SELECT * FROM Sleep WHERE id == :id")
    fun getSleepFlow(id: Int): Flow<List<SleepEntity>>

    @Query("SELECT * FROM Sleep ORDER BY id DESC LIMIT 1")
    suspend fun getCurrentSleep(): SleepEntity?

    @Query("SELECT * FROM Sleep ORDER BY id DESC LIMIT 1")
    fun getCurrentSleepFlow(): Flow<List<SleepEntity>>

    @Insert
    suspend fun insertAll(sleepList: List<SleepEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSleep(sleep: SleepEntity): Long

    @Update
    suspend fun updateSleep(sleep: SleepEntity): Int

    @Delete
    suspend fun deleteSleep(sleep: SleepEntity)

    @Query("DELETE FROM Sleep")
    suspend fun deleteAllSleep()
}
