package net.erikkarlsson.simplesleeptracker.di.module

import dagger.Binds
import dagger.Module
import net.erikkarlsson.simplesleeptracker.data.RealDateTimeProvider
import net.erikkarlsson.simplesleeptracker.domain.DateTimeProvider

@Module
abstract class DateTimeModule {

    @Binds
    abstract fun bindDateTimeProvider(realDateTimeProvider: RealDateTimeProvider): DateTimeProvider
}
