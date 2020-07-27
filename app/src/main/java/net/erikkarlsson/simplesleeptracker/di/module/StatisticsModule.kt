package net.erikkarlsson.simplesleeptracker.di.module

import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import net.erikkarlsson.simplesleeptracker.data.sleep.SleepDatabase
import net.erikkarlsson.simplesleeptracker.data.statistics.StatisticsDao
import net.erikkarlsson.simplesleeptracker.data.statistics.StatisticsRepository
import net.erikkarlsson.simplesleeptracker.domain.StatisticsDataSource
import javax.inject.Singleton

@Module
@InstallIn(ApplicationComponent::class)
abstract class StatisticsModule {

    @Binds
    @Singleton
    abstract fun bindStatisticsDataSource(statisticsRepository: StatisticsRepository): StatisticsDataSource

    companion object {

        @Provides
        @Singleton
        fun provideStatisticsDao(sleepDatabase: SleepDatabase): StatisticsDao = sleepDatabase.statisticsDao()
    }
}
