package net.erikkarlsson.simplesleeptracker.di.module

import android.content.Context
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import net.erikkarlsson.simplesleeptracker.data.sleep.SleepDao
import net.erikkarlsson.simplesleeptracker.data.sleep.SleepDatabase
import net.erikkarlsson.simplesleeptracker.data.sleep.SleepRepository
import net.erikkarlsson.simplesleeptracker.domain.SleepDataSource
import javax.inject.Singleton

@Module
@InstallIn(ApplicationComponent::class)
abstract class SleepModule {

    @Binds
    @Singleton
    abstract fun bindSleepDataSourceCoroutines(sleepRepository: SleepRepository): SleepDataSource

    companion object {

        @Provides
        @Singleton
        fun provideSleepDatabase(@ApplicationContext context: Context): SleepDatabase = SleepDatabase.getInstance(context)

        @Provides
        @Singleton
        fun provideSleepDao(sleepDatabase: SleepDatabase): SleepDao = sleepDatabase.sleepDao()

    }

}
