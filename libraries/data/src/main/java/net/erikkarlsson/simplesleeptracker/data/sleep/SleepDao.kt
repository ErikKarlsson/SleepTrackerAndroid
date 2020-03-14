package net.erikkarlsson.simplesleeptracker.data.sleep

import androidx.paging.DataSource
import androidx.room.*
import io.reactivex.Flowable
import io.reactivex.Single
import kotlinx.coroutines.flow.Flow
import net.erikkarlsson.simplesleeptracker.data.entity.SleepEntity

/**
 * Data Access Object for the sleep table.
 */
@Dao
interface SleepDao {

    @Query("SELECT count(*) FROM Sleep")
    fun getSleepCount(): Flowable<Int>

    @Query("SELECT count(*) FROM Sleep")
    fun getSleepCountFlow(): Flow<Int>

    @Query("SELECT * FROM Sleep WHERE to_date != 0 ORDER BY datetime(from_date) DESC")
    fun getSleep(): Flowable<List<SleepEntity>>

    @Query("SELECT * FROM Sleep WHERE to_date != 0 ORDER BY datetime(from_date) DESC")
    fun getSleepFactory(): DataSource.Factory<Int, SleepEntity>

    @Query("SELECT * FROM Sleep WHERE id == :id")
    fun getSleep(id: Int): Flowable<List<SleepEntity>>

    @Query("SELECT * FROM Sleep WHERE id == :id")
    fun getSleepFlow(id: Int): Flow<List<SleepEntity>>

    @Query("SELECT * FROM Sleep ORDER BY id DESC LIMIT 1")
    fun getCurrentSleep(): Flowable<List<SleepEntity>>

    @Query("SELECT * FROM Sleep ORDER BY id DESC LIMIT 1")
    fun getCurrentSleepSingle(): Single<SleepEntity>

    @Query("SELECT * FROM Sleep ORDER BY id DESC LIMIT 1")
    suspend fun getCurrentSleepCoroutines(): SleepEntity?

    @Query("SELECT * FROM Sleep ORDER BY id DESC LIMIT 1")
    fun getCurrentSleepFlow(): Flow<List<SleepEntity>>

    @Insert
    fun insertAll(sleepList: List<SleepEntity>)

    /**
     * Insert a sleep in the database. If the sleep already exists, replace it.

     * @param sleep the user toDate be inserted.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertSleep(sleep: SleepEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSleepSuspend(sleep: SleepEntity): Long

    @Update
    fun updateSleep(sleep: SleepEntity): Int

    @Update
    suspend fun updateSleepCoroutine(sleep: SleepEntity): Int

    @Delete
    fun deleteSleep(sleep: SleepEntity)

    @Delete
    suspend fun deleteSleepCoroutines(sleep: SleepEntity)

    /**
     * Delete all sleep.
     */
    @Query("DELETE FROM Sleep")
    fun deleteAllSleep()

}
