package net.erikkarlsson.simplesleeptracker.di.module

import android.content.Context
import dagger.Binds
import dagger.Module
import dagger.Provides
import net.erikkarlsson.simplesleeptracker.data.sleep.SleepDao
import net.erikkarlsson.simplesleeptracker.data.sleep.SleepDatabase
import net.erikkarlsson.simplesleeptracker.data.sleep.SleepRepository
import net.erikkarlsson.simplesleeptracker.data.sleep.SleepRepositoryCoroutines
import net.erikkarlsson.simplesleeptracker.domain.SleepDataSource
import net.erikkarlsson.simplesleeptracker.domain.SleepDataSourceCoroutines
import javax.inject.Singleton

@Module
abstract class SleepModule {

    @Binds
    @Singleton
    abstract fun bindSleepDataSource(sleepRepository: SleepRepository): SleepDataSource

    @Binds
    @Singleton
    abstract fun bindSleepDataSourceCoroutines(sleepRepository: SleepRepositoryCoroutines): SleepDataSourceCoroutines

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
