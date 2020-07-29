package net.erikkarlsson.simplesleeptracker.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import net.erikkarlsson.simplesleeptracker.domain.DateTimeProvider
import net.erikkarlsson.simplesleeptracker.testutil.MockDateTimeProvider
import javax.inject.Singleton

@Module
@InstallIn(ApplicationComponent::class)
abstract class MockDateTimeModule {

    @Binds
    @Singleton
    abstract fun bindDateTimeProvider(mockDateTimeProvider: MockDateTimeProvider): DateTimeProvider

}
