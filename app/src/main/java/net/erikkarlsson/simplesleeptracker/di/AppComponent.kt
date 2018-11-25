package net.erikkarlsson.simplesleeptracker.di

import dagger.BindsInstance
import dagger.Component
import dagger.android.AndroidInjectionModule
import net.erikkarlsson.simplesleeptracker.App
import net.erikkarlsson.simplesleeptracker.di.module.*
import net.erikkarlsson.simplesleeptracker.feature.backup.BackupSleepWorker
import net.erikkarlsson.simplesleeptracker.feature.backup.RestoreSleepWorker
import javax.inject.Singleton

@Singleton
@Component(modules = [AndroidBindingModule::class, AndroidInjectionModule::class,
    AppModule::class, BackupModule::class, DateTimeModule::class, EventModule::class,
    NotificationModule::class, PreferencesModule::class, SignInModule::class,
    StatisticsModule::class, SleepModule::class, ViewModelModule::class, WidgetModule::class])

interface AppComponent {
    @Component.Builder
    interface Builder {
        @BindsInstance
        fun application(application: App): Builder

        fun build(): AppComponent
    }

    fun inject(app: App)

    // TODO (erikkarlsson): Only needed for injecting Worker, remove once Dagger has released WorkerInjector.
    fun inject(backupSleepWorker: BackupSleepWorker)
    fun inject(restoreSleepWorker: RestoreSleepWorker)
}
