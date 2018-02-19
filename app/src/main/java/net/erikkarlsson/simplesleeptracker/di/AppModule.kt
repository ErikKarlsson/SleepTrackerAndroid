package net.erikkarlsson.simplesleeptracker.di

import android.content.Context
import dagger.Module
import dagger.Provides
import net.erikkarlsson.simplesleeptracker.App
import javax.inject.Singleton

@Module
class AppModule() {

    @Provides
    @Singleton
    fun provideApplication(app: App): Context = app
}