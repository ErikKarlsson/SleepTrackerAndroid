package net.erikkarlsson.simplesleeptracker.di.module

import dagger.Binds
import dagger.Module
import net.erikkarlsson.simplesleeptracker.data.SleepDetectionAlarm
import net.erikkarlsson.simplesleeptracker.domain.SleepDetection
import javax.inject.Singleton

@Module
abstract class SleepDetectionModule {

    @Binds
    @Singleton
    abstract fun bindSleepDetection(sleepDetectionAlarm: SleepDetectionAlarm): SleepDetection

}
