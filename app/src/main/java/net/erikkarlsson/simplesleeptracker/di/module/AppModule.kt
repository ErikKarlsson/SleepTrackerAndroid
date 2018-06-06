package net.erikkarlsson.simplesleeptracker.di.module

import android.content.Context
import androidx.work.WorkManager
import dagger.Module
import dagger.Provides
import net.erikkarlsson.simplesleeptracker.App
import javax.inject.Named
import javax.inject.Singleton

@Module
class AppModule() {

    @Provides
    @Singleton
    fun provideApplication(app: App): Context = app

    @Provides
    @Singleton
    fun provideWorkManager(): WorkManager = WorkManager.getInstance()

    @Provides
    @Singleton
    @Named("filePath")
    fun providesFilePath(context: Context): String = context.getFilesDir().getPath().toString()
}