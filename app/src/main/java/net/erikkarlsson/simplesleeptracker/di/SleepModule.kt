package net.erikkarlsson.simplesleeptracker.di

import android.content.Context
import dagger.Binds
import dagger.Module
import dagger.Provides
import net.erikkarlsson.simplesleeptracker.data.SleepDao
import net.erikkarlsson.simplesleeptracker.data.SleepDatabase
import net.erikkarlsson.simplesleeptracker.data.SleepRepository
import net.erikkarlsson.simplesleeptracker.domain.SleepDataSource
import javax.inject.Singleton

@Module
abstract class SleepModule {

    @Binds
    @Singleton
    abstract fun bindSleepDataSource(sleepRepository: SleepRepository): SleepDataSource

    @Module
    companion object {

        @JvmStatic
        @Provides
        @Singleton
        fun provideSleepDatabase(context: Context): SleepDatabase = SleepDatabase.getInstance(context)

        @JvmStatic
        @Provides
        @Singleton
        fun provideSleepDao(sleepDatabase: SleepDatabase): SleepDao = sleepDatabase.sleepDao()
    }

}