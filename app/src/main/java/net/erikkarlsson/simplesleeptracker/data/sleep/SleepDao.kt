package net.erikkarlsson.simplesleeptracker.data.sleep

import android.arch.paging.DataSource
import android.arch.persistence.room.Dao
import android.arch.persistence.room.Delete
import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy
import android.arch.persistence.room.Query
import android.arch.persistence.room.Update
import io.reactivex.Flowable
import io.reactivex.Single

/**
 * Data Access Object for the sleep table.
 */
@Dao
interface SleepDao {

    @Query("SELECT count(*) FROM Sleep")
    fun getSleepCount(): Flowable<Int>

    @Query("SELECT * FROM Sleep WHERE to_date != 0 ORDER BY datetime(from_date) DESC")
    fun getSleep(): Flowable<List<SleepEntity>>

    @Query("SELECT * FROM Sleep WHERE to_date != 0 ORDER BY datetime(from_date) DESC")
    fun getSleepFactory(): DataSource.Factory<Int, SleepEntity>

    @Query("SELECT * FROM Sleep WHERE id == :id")
    fun getSleep(id: Int): Flowable<List<SleepEntity>>

    @Query("SELECT * FROM Sleep ORDER BY id DESC LIMIT 1")
    fun getCurrentSleep(): Flowable<List<SleepEntity>>

    @Query("SELECT * FROM Sleep ORDER BY id DESC LIMIT 1")
    fun getCurrentSleepSingle(): Single<SleepEntity>

    @Insert
    fun insertAll(sleepList: List<SleepEntity>)

    /**
     * Insert a sleep in the database. If the sleep already exists, replace it.

     * @param sleep the user toDate be inserted.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertSleep(sleep: SleepEntity): Long

    @Update
    fun updateSleep(sleep: SleepEntity): Int

    @Delete
    fun deleteSleep(sleep: SleepEntity)

    /**
     * Delete all sleep.
     */
    @Query("DELETE FROM Sleep")
    fun deleteAllSleep()

}
