package net.erikkarlsson.simplesleeptracker.data

import android.arch.persistence.room.*
import io.reactivex.Flowable
import io.reactivex.Single

/**
 * Data Access Object for the sleep table.
 */
@Dao
interface SleepDao {

    @Query("SELECT * FROM Sleep ORDER BY datetime(to_date) DESC")
    fun getSleep(): Flowable<List<SleepEntity>>

    @Query("SELECT * FROM Sleep ORDER BY datetime(from_date) DESC LIMIT 1")
    fun getCurrentSleep(): Flowable<SleepEntity>

    @Query("SELECT * FROM Sleep ORDER BY datetime(from_date) DESC LIMIT 1")
    fun getCurrentSleepSingle(): Single<SleepEntity>

    @Query("SELECT ROUND(avg(hours),3) FROM Sleep")
    fun getAverageSleepInHours(): Flowable<Float>

    @Query("SELECT max(hours) FROM Sleep")
    fun getLongestSleepInHours(): Flowable<Float>

    @Query("SELECT min(hours) FROM Sleep")
    fun getShortestSleepInHours(): Flowable<Float>

    @Query("SELECT avg(from_date_midnight_offset_seconds) FROM Sleep WHERE from_date_midnight_offset_seconds != 0")
    fun getAverageBedtimeMidnightOffsetInSeconds(): Flowable<Int>

    @Query("SELECT avg(to_date_midnight_offset_seconds) FROM Sleep WHERE to_date_midnight_offset_seconds != 0")
    fun getAverageWakeupMidnightOffsetInSeconds(): Flowable<Int>

    @Query("SELECT avg(to_date_midnight_offset_seconds) FROM Sleep WHERE to_date_midnight_offset_seconds != 0 AND date(to_date) BETWEEN date(:from) AND date(:to)")
    fun getAverageWakeupMidnightOffsetInSecondsBetweenDates(from: String, to: String): Flowable<Int>

    @Query("SELECT * FROM Sleep WHERE datetime(to_date)>=datetime('now', '-2 hour')")
    fun getSleepLastMonth(): Flowable<List<SleepEntity>>

    /**
     * Insert a sleep in the database. If the sleep already exists, replace it.

     * @param sleep the user toDate be inserted.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertSleep(sleep: SleepEntity): Long

    @Update
    fun updateSleep(sleep: SleepEntity): Int

    /**
     * Delete all sleep.
     */
    @Query("DELETE FROM Sleep")
    fun deleteAllSleep()

}
