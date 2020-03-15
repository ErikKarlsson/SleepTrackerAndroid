package net.erikkarlsson.simplesleeptracker.data.statistics

import com.google.common.collect.ImmutableList
import kotlinx.coroutines.flow.*
import net.easypark.dateutil.midnightOffsetToLocalTime
import net.erikkarlsson.simplesleeptracker.data.entity.SleepEntity
import net.erikkarlsson.simplesleeptracker.data.sleep.SleepMapper
import net.erikkarlsson.simplesleeptracker.domain.StatisticsDataSource
import net.erikkarlsson.simplesleeptracker.domain.entity.*
import org.threeten.bp.LocalTime
import javax.inject.Inject

class StatisticsRepository @Inject constructor(private val statisticsDao: StatisticsDaoCoroutines,
                                               private val sleepMapper: SleepMapper)
    : StatisticsDataSource {

    override fun getYoungestSleep(): Flow<Sleep> =
            statisticsDao.getYoungestSleep()
                    .map(::firstInListOrEmpty)
                    .map { sleepMapper.mapFromEntity(it) }

    override fun getOldestSleep(): Flow<Sleep> =
            statisticsDao.getOldestSleep()
                    .map(::firstInListOrEmpty)
                    .map { sleepMapper.mapFromEntity(it) }

    override fun getStatistics(): Flow<Statistics> {
        val infiniteDateRange = DateRange.infinite()
        return getStatistics(infiniteDateRange)
    }

    override fun getStatistics(dateRange: DateRange): Flow<Statistics> {
        val from = dateRange.from.toString()
        val to = dateRange.to.toString()

        val flowList = listOf(
                getAverageSleepInHours(from, to),
                getLongestSleep(from, to),
                getShortestSleep(from, to),
                getAverageWakeUpTime(from, to),
                getAverageBedtime(from, to),
                getAverageBedtimeDayOfWeek(from, to),
                getAverageWakeUpTimeDayOfWeek(from, to),
                getAverageSleepDurationDayOfWeek(from, to)
        )

        return getSleepCount(from, to)
                .flatMapLatest { sleepCount ->
                    if (sleepCount == 0) {
                        flow { Statistics.empty() }
                    } else {
                        combine(flowList) {
                            Statistics(sleepCount,
                                    it[0] as Float,
                                    it[1] as Sleep,
                                    it[2] as Sleep,
                                    it[3] as LocalTime,
                                    it[4] as LocalTime,
                                    it[5] as ImmutableList<DayOfWeekLocalTime>,
                                    it[6] as ImmutableList<DayOfWeekLocalTime>,
                                    it[7] as ImmutableList<DayOfWeekHours>
                            )
                        }
                    }
                }
    }

    override fun getSleepCountYearMonth(): Flow<List<SleepCountYearMonth>> =
            statisticsDao.getCountGroupedByYearMonth()

    private suspend fun firstInListOrEmpty(sleepList: List<SleepEntity>): SleepEntity =
            sleepList.firstOrNull() ?: SleepEntity.empty()

    private fun getSleepCount(from: String, to: String): Flow<Int> =
            statisticsDao.getSleepCount(from, to)

    private fun getAverageSleepInHours(from: String, to: String): Flow<Float> =
            statisticsDao.getAverageSleepInHours(from, to)

    private fun getAverageSleepDurationDayOfWeek(from: String, to: String): Flow<ImmutableList<DayOfWeekHours>> =
            statisticsDao.getAverageSleepDurationDayOfWeek(from, to).map { ImmutableList.copyOf(it) }

    private fun getLongestSleep(from: String, to: String): Flow<Sleep> =
            statisticsDao.getLongestSleep(from, to).map { sleepMapper.mapFromEntity(it) }

    private fun getShortestSleep(from: String, to: String): Flow<Sleep> =
            statisticsDao.getShortestSleep(from, to).map { sleepMapper.mapFromEntity(it) }

    private fun getAverageWakeUpTime(from: String, to: String): Flow<LocalTime> =
            statisticsDao.getAverageWakeUpMidnightOffsetInSeconds(from, to).map { it.midnightOffsetToLocalTime }

    private fun getAverageBedtime(from: String, to: String): Flow<LocalTime> =
            statisticsDao.getAverageBedtimeMidnightOffsetInSeconds(from, to).map { it.midnightOffsetToLocalTime }

    private fun getAverageBedtimeDayOfWeek(from: String, to: String): Flow<ImmutableList<DayOfWeekLocalTime>> =
            statisticsDao.getAverageBedtimeMidnightOffsetInSecondsForDaysOfWeek(from, to)
                    .map { toDayOfWeekLocalTimeListFlow(it) }

    private fun getAverageWakeUpTimeDayOfWeek(from: String, to: String): Flow<ImmutableList<DayOfWeekLocalTime>> =
            statisticsDao.getAverageWakeUpMidnightOffsetInSecondsForDaysOfWeek(from, to)
                    .map { toDayOfWeekLocalTimeListFlow(it) }

    private fun toDayOfWeekLocalTimeListFlow(dayOfWeekMidnightOffset: List<DayOfWeekMidnightOffset>): ImmutableList<DayOfWeekLocalTime> {
        val list = dayOfWeekMidnightOffset.map { it.toDayOfWeekLocalTime }.toList()
        return ImmutableList.builder<DayOfWeekLocalTime>().addAll(list).build()
    }
}

