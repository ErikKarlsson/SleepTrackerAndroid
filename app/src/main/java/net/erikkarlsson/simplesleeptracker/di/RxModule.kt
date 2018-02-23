package net.erikkarlsson.simplesleeptracker.di

import dagger.Binds
import dagger.Module
import net.erikkarlsson.simplesleeptracker.util.RxSchedulerProvider
import net.erikkarlsson.simplesleeptracker.util.SchedulerProvider

@Module
abstract class RxModule {

    @Binds
    abstract fun bindSchedulerProvider(rxSchedulerProvider: RxSchedulerProvider): SchedulerProvider

}