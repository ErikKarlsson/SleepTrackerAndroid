package net.erikkarlsson.simplesleeptracker.di.module

import android.arch.lifecycle.MutableLiveData
import dagger.Module
import dagger.Provides
import net.erikkarlsson.simplesleeptracker.base.Event
import javax.inject.Named
import javax.inject.Singleton

@Module
class EventModule {

    @Provides
    @Singleton
    @Named("sleepAddedEvents")
    fun providesSleepAddedEvents(): MutableLiveData<Event<Unit>> = MutableLiveData()

}