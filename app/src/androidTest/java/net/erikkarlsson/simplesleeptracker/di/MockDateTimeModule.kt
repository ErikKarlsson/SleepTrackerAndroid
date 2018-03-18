package net.erikkarlsson.simplesleeptracker.di

import dagger.Binds
import dagger.Module
import net.erikkarlsson.simplesleeptracker.base.MockDateTimeProvider
import net.erikkarlsson.simplesleeptracker.domain.DateTimeProvider
import javax.inject.Singleton

@Module
abstract class MockDateTimeModule {

    @Binds
    @Singleton
    abstract fun bindDateTimeProvider(mockDateTimeProvider: MockDateTimeProvider): DateTimeProvider

}