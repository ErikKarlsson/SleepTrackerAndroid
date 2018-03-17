package net.erikkarlsson.simplesleeptracker.data

import android.arch.persistence.room.*
import io.reactivex.Flowable
import io.reactivex.Single

/**
 * Data Access Object for the sleep table.
 */
@Dao
interface SleepDao {

    @Query("SELECT count(*) FROM Sleep WHERE to_date != 0 AND date(to_date) BETWEEN date(:from) AND date(:to)")
    fun getSleepCount(from: String, to: String): Flowable<Int>

    @Query("SELECT * FROM Sleep ORDER BY datetime(from_date) DESC")
    fun getSleep(): Flowable<List<SleepEntity>>

    @Query("SELECT * FROM Sleep ORDER BY datetime(from_date) DESC LIMIT 1")
    fun getCurrentSleep(): Flowable<SleepEntity>

    @Query("SELECT * FROM Sleep ORDER BY datetime(from_date) DESC LIMIT 1")
    fun getCurrentSleepSingle(): Single<SleepEntity>

    @Query("SELECT ROUND(avg(hours),3) FROM Sleep WHERE to_date != 0 AND date(to_date) BETWEEN date(:from) AND date(:to)")
    fun getAverageSleepInHours(from: String, to: String): Flowable<Float>

    @Query("SELECT * FROM Sleep WHERE to_date != 0 AND date(to_date) BETWEEN date(:from) AND date(:to) AND hours = (SELECT max(hours) FROM Sleep WHERE to_date != 0 AND date(to_date) BETWEEN date(:from) AND date(:to)) ORDER BY datetime(to_date) ASC LIMIT 1")
    fun getLongestSleep(from: String, to: String): Flowable<SleepEntity>

    @Query("SELECT * FROM Sleep WHERE to_date != 0 AND date(to_date) BETWEEN date(:from) AND date(:to) AND hours = (SELECT min(hours) FROM Sleep WHERE to_date != 0 AND date(to_date) BETWEEN date(:from) AND date(:to)) ORDER BY datetime(to_date) ASC LIMIT 1")
    fun getShortestSleep(from: String, to: String): Flowable<SleepEntity>

    @Query("SELECT avg(from_date_midnight_offset_seconds) as 'midnightOffsetInSeconds', strftime('%w', to_date) as 'dayOfWeek' FROM Sleep WHERE to_date != 0 AND date(to_date) BETWEEN date(:from) AND date(:to) GROUP BY strftime('%w', to_date)")
    fun getAverageBedtimeMidnightOffsetInSecondsForDaysOfWeek(from: String, to: String): Flowable<List<DayOfWeekMidnightOffset>>

    @Query("SELECT avg(from_date_midnight_offset_seconds) FROM Sleep WHERE to_date != 0 AND date(to_date) BETWEEN date(:from) AND date(:to)")
    fun getAverageBedtimeMidnightOffsetInSeconds(from: String, to: String): Flowable<Int>

    @Query("SELECT avg(to_date_midnight_offset_seconds) as 'midnightOffsetInSeconds', strftime('%w', to_date) as 'dayOfWeek' FROM Sleep WHERE to_date != 0 AND date(to_date) BETWEEN date(:from) AND date(:to) GROUP BY strftime('%w', to_date)")
    fun getAverageWakeupMidnightOffsetInSecondsForDaysOfWeek(from: String, to: String): Flowable<List<DayOfWeekMidnightOffset>>

    @Query("SELECT avg(to_date_midnight_offset_seconds) FROM Sleep WHERE to_date != 0 AND date(to_date) BETWEEN date(:from) AND date(:to)")
    fun getAverageWakeupMidnightOffsetInSeconds(from: String, to: String): Flowable<Int>

    @Query("SELECT avg(to_date_midnight_offset_seconds) FROM Sleep WHERE to_date != 0 AND date(to_date) BETWEEN date(:from) AND date(:to)")
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
