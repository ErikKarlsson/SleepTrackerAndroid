package net.erikkarlsson.simplesleeptracker.di.module

import dagger.Binds
import dagger.Module
import dagger.Provides
import net.erikkarlsson.simplesleeptracker.data.SleepDatabase
import net.erikkarlsson.simplesleeptracker.data.sleepdetection.SleepDetectionAlarm
import net.erikkarlsson.simplesleeptracker.data.sleepdetection.DetectionActionRepository
import net.erikkarlsson.simplesleeptracker.data.sleepdetection.DetectionDao
import net.erikkarlsson.simplesleeptracker.domain.DetectionActionDataSource
import net.erikkarlsson.simplesleeptracker.domain.SleepDetection
import javax.inject.Singleton

@Module
abstract class SleepDetectionModule {

    @Binds
    @Singleton
    abstract fun bindSleepDetection(sleepDetectionAlarm: SleepDetectionAlarm): SleepDetection

    @Binds
    @Singleton
    abstract fun bindDetectionActionDataSource(detectionActionRepository: DetectionActionRepository): DetectionActionDataSource

    @Module
    companion object {

        @JvmStatic
        @Provides
        @Singleton
        fun provideDetectionDao(sleepDatabase: SleepDatabase): DetectionDao = sleepDatabase.detectionDao()
    }
}
