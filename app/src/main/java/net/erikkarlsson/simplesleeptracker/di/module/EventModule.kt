package net.erikkarlsson.simplesleeptracker.di.module

import androidx.lifecycle.MutableLiveData
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.channels.BroadcastChannel
import net.erikkarlsson.simplesleeptracker.core.livedata.Event
import net.erikkarlsson.simplesleeptracker.domain.entity.SleepEvent
import net.erikkarlsson.simplesleeptracker.features.home.HomeEvents
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class EventModule {

    @Provides
    @Singleton
    @Named("sleepAddedEvents")
    fun providesSleepAddedEvents(): BroadcastChannel<Event<Unit>> = BroadcastChannel(1)

    @Provides
    @Singleton
    @Named("homeEvents")
    fun providesHomeEvents(): HomeEvents = MutableLiveData()

    @Provides
    @Singleton
    @Named("sleepEvents")
    fun providesSleepEventChannel(): BroadcastChannel<SleepEvent> = BroadcastChannel(1)

}
