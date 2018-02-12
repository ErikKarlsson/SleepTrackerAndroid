package net.erikkarlsson.simplesleeptracker.persistence

import android.arch.persistence.room.*
import io.reactivex.Flowable
import io.reactivex.Maybe

/**
 * Data Access Object for the sleep table.
 */
@Dao
interface SleepDao {

    @Query("SELECT * FROM Sleep ORDER BY datetime(wake_up_time) DESC")
    fun getSleep(): Flowable<List<Sleep>>

    @Query("SELECT * FROM Sleep ORDER BY datetime(bed_time) DESC LIMIT 1")
    fun getCurrentSleep(): Maybe<Sleep>

    @Query("SELECT * FROM Sleep WHERE datetime(wake_up_time)>=datetime('now', '-2 hour')")
    fun getSleepLastMonth(): Flowable<List<Sleep>>

    /**
     * Insert a sleep in the database. If the sleep already exists, replace it.

     * @param sleep the user to be inserted.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertSleep(sleep: Sleep): Long

    @Update
    fun updateSleep(sleep: Sleep): Int

    /**
     * Delete all sleep.
     */
    @Query("DELETE FROM Sleep")
    fun deleteAllSleep()

}
