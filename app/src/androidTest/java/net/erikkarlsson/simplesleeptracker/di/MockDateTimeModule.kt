package net.erikkarlsson.simplesleeptracker.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn
import net.erikkarlsson.simplesleeptracker.di.module.DateTimeModule
import net.erikkarlsson.simplesleeptracker.domain.DateTimeProvider
import net.erikkarlsson.simplesleeptracker.testutil.MockDateTimeProvider
import javax.inject.Singleton

@Module
@TestInstallIn(
        components = [SingletonComponent::class],
        replaces = [DateTimeModule::class])
abstract class MockDateTimeModule {

    @Binds
    @Singleton
    abstract fun bindDateTimeProvider(mockDateTimeProvider: MockDateTimeProvider): DateTimeProvider

}
