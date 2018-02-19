package net.erikkarlsson.simplesleeptracker.di

import android.content.Context
import dagger.Module
import dagger.Provides
import net.erikkarlsson.simplesleeptracker.data.SleepDao
import net.erikkarlsson.simplesleeptracker.data.SleepDatabase
import javax.inject.Singleton

@Module
class SleepModule {

    @Provides
    @Singleton
    fun provideSleepDatabase(context: Context): SleepDatabase = SleepDatabase.getInstance(context)

    @Provides
    @Singleton
    fun provideSleepDao(sleepDatabase: SleepDatabase): SleepDao = sleepDatabase.sleepDao()

}