package net.erikkarlsson.simplesleeptracker.di.module

import android.content.Context
import dagger.Binds
import dagger.Module
import dagger.Provides
import net.erikkarlsson.simplesleeptracker.AndroidAppLifecycle
import net.erikkarlsson.simplesleeptracker.App
import net.erikkarlsson.simplesleeptracker.core.DefaultDispatcherProvider
import net.erikkarlsson.simplesleeptracker.core.DispatcherProvider
import net.erikkarlsson.simplesleeptracker.domain.AppLifecycle
import javax.inject.Named
import javax.inject.Singleton

@Module
abstract class AppModule {

    @Binds
    abstract fun bindsAppLifecycle(appLifecycle: AndroidAppLifecycle): AppLifecycle

    @Module
    companion object {
        @Provides
        @Singleton
        @JvmStatic
        fun providesApplication(app: App): Context = app

        @Provides
        @Singleton
        @JvmStatic
        fun providesResources(context: Context) = context.resources

        @Provides
        @Singleton
        @JvmStatic
        @Named("filePath")
        fun providesFilePath(context: Context): String = context.getFilesDir().getPath().toString()

        @Provides
        @JvmStatic
        fun providesDispatcherProvider(): DispatcherProvider = DefaultDispatcherProvider()
    }

}
