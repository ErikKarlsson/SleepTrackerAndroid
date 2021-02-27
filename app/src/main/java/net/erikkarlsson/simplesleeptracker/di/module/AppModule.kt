package net.erikkarlsson.simplesleeptracker.di.module

import android.content.Context
import android.content.res.Resources
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import dagger.hilt.migration.DisableInstallInCheck
import net.erikkarlsson.simplesleeptracker.AndroidAppLifecycle
import net.erikkarlsson.simplesleeptracker.core.DefaultDispatcherProvider
import net.erikkarlsson.simplesleeptracker.core.DispatcherProvider
import net.erikkarlsson.simplesleeptracker.domain.AppLifecycle
import javax.inject.Named
import javax.inject.Singleton


/**
 * Hilt requires included modules to be annotated with [InstallIn], as issues due to forgetting it are potentially difficult to track.
 *
 * However, since the included [AssistedInject_AppModule] is auto-generated, for now we need to disable this check. Hilt allows us
 * to do it via [DisableInstallInCheck], along with the `disableModulesHaveInstallInCheck` compiler option declared in the module's build.gradle.
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class AppModule {

    @Binds
    abstract fun bindsAppLifecycle(appLifecycle: AndroidAppLifecycle): AppLifecycle

    companion object {

        @Provides
        @Singleton
        fun providesResources(@ApplicationContext context: Context): Resources = context.resources

        @Provides
        @Singleton
        @Named("filePath")
        fun providesFilePath(@ApplicationContext context: Context): String = context.getFilesDir().getPath().toString()

        @Provides
        fun providesDispatcherProvider(): DispatcherProvider = DefaultDispatcherProvider()
    }

}
