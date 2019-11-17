package net.erikkarlsson.simplesleeptracker.di

import dagger.BindsInstance
import dagger.Component
import dagger.android.AndroidInjectionModule
import net.erikkarlsson.simplesleeptracker.App
import net.erikkarlsson.simplesleeptracker.di.module.*
import net.erikkarlsson.simplesleeptracker.features.backup.BackupSleepWorker
import net.erikkarlsson.simplesleeptracker.features.backup.RestoreSleepWorker
import net.erikkarlsson.simplesleeptracker.features.backup.di.BackupAssistedModule
import net.erikkarlsson.simplesleeptracker.features.details.di.AddAssistedModule
import net.erikkarlsson.simplesleeptracker.features.details.di.DetailsAssistedModule
import net.erikkarlsson.simplesleeptracker.features.diary.di.DiaryAssistedModule
import net.erikkarlsson.simplesleeptracker.features.home.di.module.HomeAssistedModule
import net.erikkarlsson.simplesleeptracker.features.statistics.di.StatisticsAssistedModule
import javax.inject.Singleton

@Singleton
@Component(modules = [AndroidBindingModule::class, AndroidInjectionModule::class,
    AppAssistedModule::class, AppModule::class, BackupModule::class, DateTimeModule::class,
    EventModule::class, NotificationModule::class, PreferencesModule::class, SignInModule::class,
    StatisticsModule::class, SleepModule::class, WidgetModule::class,
    HomeAssistedModule::class, DiaryAssistedModule::class, DetailsAssistedModule::class,
    AddAssistedModule::class, StatisticsAssistedModule::class, WorkerBindingModule::class,
    BackupAssistedModule::class
])

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
