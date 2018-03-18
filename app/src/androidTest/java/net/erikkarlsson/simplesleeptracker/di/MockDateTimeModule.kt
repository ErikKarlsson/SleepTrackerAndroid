package net.erikkarlsson.simplesleeptracker.di

import dagger.Binds
import dagger.Module
import net.erikkarlsson.simplesleeptracker.domain.DateTimeProvider
import net.erikkarlsson.simplesleeptracker.util.MockDateTimeProvider
import javax.inject.Singleton

@Module
abstract class MockDateTimeModule {

    @Binds
    @Singleton
    abstract fun bindDateTimeProvider(mockDateTimeProvider: MockDateTimeProvider): DateTimeProvider

}