package net.erikkarlsson.simplesleeptracker.di.module

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import net.erikkarlsson.simplesleeptracker.data.RealDateTimeProvider
import net.erikkarlsson.simplesleeptracker.domain.DateTimeProvider

@Module
@InstallIn(ApplicationComponent::class)
abstract class DateTimeModule {

    @Binds
    abstract fun bindDateTimeProvider(realDateTimeProvider: RealDateTimeProvider): DateTimeProvider
}
