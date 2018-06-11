package net.erikkarlsson.simplesleeptracker.data

import androidx.paging.DataSource
import androidx.room.*
import io.reactivex.Flowable
import io.reactivex.Single

/**
 * Data Access Object for the sleep table.
 */
@Dao
interface SleepDao {

    @Query("SELECT count(*) FROM Sleep")
    fun getSleepCount(): Flowable<Int>

    @Query("SELECT count(*) FROM Sleep WHERE to_date != 0 AND date(to_date) BETWEEN date(:from) AND date(:to)")
    fun getSleepCount(from: String, to: String): Flowable<Int>

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

    @Query("SELECT ROUND(avg(hours),3) FROM Sleep WHERE to_date != 0 AND date(to_date) BETWEEN date(:from) AND date(:to)")
    fun getAverageSleepInHours(from: String, to: String): Flowable<Float>

    @Query("SELECT * FROM Sleep WHERE to_date != 0 AND date(to_date) BETWEEN date(:from) AND date(:to) AND hours = (SELECT max(hours) FROM Sleep WHERE to_date != 0 AND date(to_date) BETWEEN date(:from) AND date(:to)) ORDER BY datetime(to_date) ASC LIMIT 1")
    fun getLongestSleep(from: String, to: String): Flowable<SleepEntity>

    @Query("SELECT * FROM Sleep WHERE to_date != 0 AND date(to_date) BETWEEN date(:from) AND date(:to) AND hours = (SELECT min(hours) FROM Sleep WHERE to_date != 0 AND date(to_date) BETWEEN date(:from) AND date(:to)) ORDER BY datetime(to_date) ASC LIMIT 1")
    fun getShortestSleep(from: String, to: String): Flowable<SleepEntity>

    @Query("SELECT avg(from_date_midnight_offset_seconds) as 'midnightOffsetInSeconds', strftime('%w', from_date) as 'dayOfWeek' FROM Sleep WHERE to_date != 0 AND date(from_date) BETWEEN date(:from) AND date(:to) GROUP BY strftime('%w', to_date)")
    fun getAverageBedtimeMidnightOffsetInSecondsForDaysOfWeek(from: String, to: String): Flowable<List<DayOfWeekMidnightOffset>>

    @Query("SELECT avg(from_date_midnight_offset_seconds) FROM Sleep WHERE to_date != 0 AND date(from_date) BETWEEN date(:from) AND date(:to)")
    fun getAverageBedtimeMidnightOffsetInSeconds(from: String, to: String): Flowable<Int>

    @Query("SELECT avg(to_date_midnight_offset_seconds) as 'midnightOffsetInSeconds', strftime('%w', to_date) as 'dayOfWeek' FROM Sleep WHERE to_date != 0 AND date(to_date) BETWEEN date(:from) AND date(:to) GROUP BY strftime('%w', to_date)")
    fun getAverageWakeUpMidnightOffsetInSecondsForDaysOfWeek(from: String, to: String): Flowable<List<DayOfWeekMidnightOffset>>

    @Query("SELECT avg(to_date_midnight_offset_seconds) FROM Sleep WHERE to_date != 0 AND date(to_date) BETWEEN date(:from) AND date(:to)")
    fun getAverageWakeUpMidnightOffsetInSeconds(from: String, to: String): Flowable<Int>

    @Query("SELECT avg(to_date_midnight_offset_seconds) FROM Sleep WHERE to_date != 0 AND date(to_date) BETWEEN date(:from) AND date(:to)")
    fun getAverageWakeUpMidnightOffsetInSecondsBetweenDates(from: String, to: String): Flowable<Int>

    @Query("SELECT * FROM Sleep WHERE datetime(to_date)>=datetime('now', '-2 hour')")
    fun getSleepLastMonth(): Flowable<List<SleepEntity>>

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
