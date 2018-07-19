package net.erikkarlsson.simplesleeptracker.di

import dagger.BindsInstance
import dagger.Component
import dagger.android.AndroidInjectionModule
import net.erikkarlsson.simplesleeptracker.App
import net.erikkarlsson.simplesleeptracker.di.module.AndroidBindingModule
import net.erikkarlsson.simplesleeptracker.di.module.AppModule
import net.erikkarlsson.simplesleeptracker.di.module.BackupModule
import net.erikkarlsson.simplesleeptracker.di.module.DateTimeModule
import net.erikkarlsson.simplesleeptracker.di.module.EventModule
import net.erikkarlsson.simplesleeptracker.di.module.PreferencesModule
import net.erikkarlsson.simplesleeptracker.di.module.SignInModule
import net.erikkarlsson.simplesleeptracker.di.module.SleepModule
import net.erikkarlsson.simplesleeptracker.di.module.ViewModelModule
import net.erikkarlsson.simplesleeptracker.feature.backup.BackupSleepWorker
import net.erikkarlsson.simplesleeptracker.feature.backup.RestoreSleepWorker
import javax.inject.Singleton

@Singleton
@Component(modules = [AndroidBindingModule::class, AndroidInjectionModule::class,
    AppModule::class, BackupModule::class, DateTimeModule::class, EventModule::class,
    PreferencesModule::class, SignInModule::class, SleepModule::class, ViewModelModule::class])

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