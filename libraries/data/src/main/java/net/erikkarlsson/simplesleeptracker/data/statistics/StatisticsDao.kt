package net.erikkarlsson.simplesleeptracker.data.statistics

import androidx.room.Dao
import androidx.room.Query
import io.reactivex.Flowable
import net.erikkarlsson.simplesleeptracker.data.entity.SleepEntity
import net.erikkarlsson.simplesleeptracker.domain.entity.DayOfWeekHours
import net.erikkarlsson.simplesleeptracker.domain.entity.DayOfWeekMidnightOffset
import net.erikkarlsson.simplesleeptracker.domain.entity.SleepCountYearMonth

/**
 * Data Access Object for the sleep table.
 */
@Dao
interface StatisticsDao {

    @Query("SELECT count(*) FROM Sleep WHERE to_date != 0 AND date(to_date_local) BETWEEN date(:from) AND date(:to)")
    fun getSleepCount(from: String, to: String): Flowable<Int>

    @Query("SELECT ROUND(avg(hours),3) FROM Sleep WHERE to_date != 0 AND date(to_date_local) BETWEEN date(:from) AND date(:to)")
    fun getAverageSleepInHours(from: String, to: String): Flowable<Float>

    @Query("SELECT ROUND(avg(hours),3) as 'hours', strftime('%w', to_date_local) as 'day' FROM Sleep WHERE to_date != 0 AND date(to_date_local) BETWEEN date(:from) AND date(:to) GROUP BY strftime('%w', to_date_local)")
    fun getAverageSleepDurationDayOfWeek(from: String, to: String): Flowable<List<DayOfWeekHours>>

    @Query("SELECT * FROM Sleep WHERE to_date != 0 AND date(to_date_local) BETWEEN date(:from) AND date(:to) AND hours = (SELECT max(hours) FROM Sleep WHERE to_date != 0 AND date(to_date_local) BETWEEN date(:from) AND date(:to)) ORDER BY date(to_date_local) ASC LIMIT 1")
    fun getLongestSleep(from: String, to: String): Flowable<SleepEntity>

    @Query("SELECT * FROM Sleep WHERE to_date != 0 AND date(to_date_local) BETWEEN date(:from) AND date(:to) AND hours = (SELECT min(hours) FROM Sleep WHERE to_date != 0 AND date(to_date_local) BETWEEN date(:from) AND date(:to)) ORDER BY date(to_date_local) ASC LIMIT 1")
    fun getShortestSleep(from: String, to: String): Flowable<SleepEntity>

    @Query("SELECT avg(from_date_midnight_offset_seconds) as 'midnightOffsetInSeconds', strftime('%w', to_date_local) as 'day' FROM Sleep WHERE to_date != 0 AND date(to_date_local) BETWEEN date(:from) AND date(:to) GROUP BY strftime('%w', to_date_local)")
    fun getAverageBedtimeMidnightOffsetInSecondsForDaysOfWeek(from: String, to: String): Flowable<List<DayOfWeekMidnightOffset>>

    @Query("SELECT avg(from_date_midnight_offset_seconds) FROM Sleep WHERE to_date != 0 AND date(to_date_local) BETWEEN date(:from) AND date(:to)")
    fun getAverageBedtimeMidnightOffsetInSeconds(from: String, to: String): Flowable<Int>

    @Query("SELECT avg(to_date_midnight_offset_seconds) as 'midnightOffsetInSeconds', strftime('%w', to_date_local) as 'day' FROM Sleep WHERE to_date != 0 AND date(to_date_local) BETWEEN date(:from) AND date(:to) GROUP BY strftime('%w', to_date_local)")
    fun getAverageWakeUpMidnightOffsetInSecondsForDaysOfWeek(from: String, to: String): Flowable<List<DayOfWeekMidnightOffset>>

    @Query("SELECT count(*) as 'sleepCount', strftime('%Y', from_date_local) as 'yearString', strftime('%m', from_date_local) as 'monthString' FROM Sleep WHERE to_date != 0 GROUP BY strftime('%Y', from_date_local), strftime('%m', from_date_local)")
    fun getCountGroupedByYearMonth(): Flowable<List<SleepCountYearMonth>>

    @Query("SELECT avg(to_date_midnight_offset_seconds) FROM Sleep WHERE to_date != 0 AND date(to_date_local) BETWEEN date(:from) AND date(:to)")
    fun getAverageWakeUpMidnightOffsetInSeconds(from: String, to: String): Flowable<Int>

    @Query("SELECT * FROM Sleep WHERE to_date != 0 ORDER BY date(to_date_local) ASC LIMIT 1")
    fun getYoungestSleep(): Flowable<List<SleepEntity>>

    @Query("SELECT * FROM Sleep WHERE to_date != 0 ORDER BY date(to_date_local) DESC LIMIT 1")
    fun getOldestSleep(): Flowable<List<SleepEntity>>
}
