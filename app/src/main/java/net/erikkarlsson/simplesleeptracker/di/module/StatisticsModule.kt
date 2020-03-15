package net.erikkarlsson.simplesleeptracker.di.module

import dagger.Binds
import dagger.Module
import dagger.Provides
import net.erikkarlsson.simplesleeptracker.data.sleep.SleepDatabase
import net.erikkarlsson.simplesleeptracker.data.statistics.StatisticsDao
import net.erikkarlsson.simplesleeptracker.data.statistics.StatisticsDaoCoroutines
import net.erikkarlsson.simplesleeptracker.data.statistics.StatisticsRepository
import net.erikkarlsson.simplesleeptracker.domain.StatisticsDataSource
import javax.inject.Singleton

@Module
abstract class StatisticsModule {

    @Binds
    @Singleton
    abstract fun bindStatisticsDataSource(statisticsRepository: StatisticsRepository): StatisticsDataSource

    @Module
    companion object {

        @JvmStatic
        @Provides
        @Singleton
        fun provideStatisticsDao(sleepDatabase: SleepDatabase): StatisticsDao = sleepDatabase.statisticsDao()

        @JvmStatic
        @Provides
        @Singleton
        fun provideStatisticsDaoCoroutines(sleepDatabase: SleepDatabase): StatisticsDaoCoroutines = sleepDatabase.statisticsDaoCoroutines()
    }
}
