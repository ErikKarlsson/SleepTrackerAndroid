package net.erikkarlsson.simplesleeptracker.data.statistics

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Query
import io.reactivex.Flowable
import net.erikkarlsson.simplesleeptracker.data.DayOfWeekHours
import net.erikkarlsson.simplesleeptracker.data.DayOfWeekMidnightOffset
import net.erikkarlsson.simplesleeptracker.data.sleep.SleepEntity
import net.erikkarlsson.simplesleeptracker.domain.entity.SleepCountYearMonth

/**
 * Data Access Object for the sleep table.
 */
@Dao
interface StatisticsDao {

    @Query("SELECT count(*) FROM Sleep WHERE to_date != 0 AND date(to_date) BETWEEN date(:from) AND date(:to)")
    fun getSleepCount(from: String, to: String): Flowable<Int>

    @Query("SELECT ROUND(avg(hours),3) FROM Sleep WHERE to_date != 0 AND date(to_date) BETWEEN date(:from) AND date(:to)")
    fun getAverageSleepInHours(from: String, to: String): Flowable<Float>

    @Query("SELECT ROUND(avg(hours),3) as 'hours', strftime('%w', from_date) as 'day' FROM Sleep WHERE to_date != 0 AND date(to_date) BETWEEN date(:from) AND date(:to) GROUP BY strftime('%w', to_date)")
    fun getAverageSleepDurationDayOfWeek(from: String, to: String): Flowable<List<DayOfWeekHours>>

    @Query("SELECT * FROM Sleep WHERE to_date != 0 AND date(to_date) BETWEEN date(:from) AND date(:to) AND hours = (SELECT max(hours) FROM Sleep WHERE to_date != 0 AND date(to_date) BETWEEN date(:from) AND date(:to)) ORDER BY datetime(to_date) ASC LIMIT 1")
    fun getLongestSleep(from: String, to: String): Flowable<SleepEntity>

    @Query("SELECT * FROM Sleep WHERE to_date != 0 AND date(to_date) BETWEEN date(:from) AND date(:to) AND hours = (SELECT min(hours) FROM Sleep WHERE to_date != 0 AND date(to_date) BETWEEN date(:from) AND date(:to)) ORDER BY datetime(to_date) ASC LIMIT 1")
    fun getShortestSleep(from: String, to: String): Flowable<SleepEntity>

    @Query("SELECT avg(from_date_midnight_offset_seconds) as 'midnightOffsetInSeconds', strftime('%w', from_date) as 'day' FROM Sleep WHERE to_date != 0 AND date(from_date) BETWEEN date(:from) AND date(:to) GROUP BY strftime('%w', to_date)")
    fun getAverageBedtimeMidnightOffsetInSecondsForDaysOfWeek(from: String, to: String): Flowable<List<DayOfWeekMidnightOffset>>

    @Query("SELECT avg(from_date_midnight_offset_seconds) FROM Sleep WHERE to_date != 0 AND date(from_date) BETWEEN date(:from) AND date(:to)")
    fun getAverageBedtimeMidnightOffsetInSeconds(from: String, to: String): Flowable<Int>

    @Query("SELECT avg(to_date_midnight_offset_seconds) as 'midnightOffsetInSeconds', strftime('%w', to_date) as 'day' FROM Sleep WHERE to_date != 0 AND date(to_date) BETWEEN date(:from) AND date(:to) GROUP BY strftime('%w', to_date)")
    fun getAverageWakeUpMidnightOffsetInSecondsForDaysOfWeek(from: String, to: String): Flowable<List<DayOfWeekMidnightOffset>>

    @Query("SELECT count(*) as 'sleepCount', strftime('%Y', from_date) as 'yearString', strftime('%m', from_date) as 'monthString' FROM Sleep WHERE to_date != 0 GROUP BY strftime('%Y', from_date), strftime('%m', from_date)")
    fun getCountGroupedByYearMonth(): Flowable<List<SleepCountYearMonth>>

    @Query("SELECT avg(to_date_midnight_offset_seconds) FROM Sleep WHERE to_date != 0 AND date(to_date) BETWEEN date(:from) AND date(:to)")
    fun getAverageWakeUpMidnightOffsetInSeconds(from: String, to: String): Flowable<Int>

}
