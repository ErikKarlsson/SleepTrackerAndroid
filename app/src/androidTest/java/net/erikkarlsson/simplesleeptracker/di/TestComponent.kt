package net.erikkarlsson.simplesleeptracker.di

import dagger.BindsInstance
import dagger.Component
import dagger.android.AndroidInjectionModule
import net.erikkarlsson.simplesleeptracker.App
import net.erikkarlsson.simplesleeptracker.TestApp
import net.erikkarlsson.simplesleeptracker.di.module.*
import net.erikkarlsson.simplesleeptracker.features.appwidget.di.WidgetModule
import net.erikkarlsson.simplesleeptracker.features.backup.di.BackupAssistedModule
import net.erikkarlsson.simplesleeptracker.features.backup.di.BackupModule
import net.erikkarlsson.simplesleeptracker.features.backup.di.WorkerBindingModule
import net.erikkarlsson.simplesleeptracker.features.details.di.AddAssistedModule
import net.erikkarlsson.simplesleeptracker.features.details.di.DetailsAssistedModule
import net.erikkarlsson.simplesleeptracker.features.diary.di.DiaryAssistedModule
import net.erikkarlsson.simplesleeptracker.features.home.di.module.HomeAssistedModule
import net.erikkarlsson.simplesleeptracker.features.statistics.di.StatisticsAssistedModule
import javax.inject.Singleton

@Singleton
@Component(modules = [AndroidBindingModule::class, AndroidInjectionModule::class,
    AppAssistedModule::class, AppModule::class, BackupModule::class, EventModule::class,
    NotificationModule::class, MockDateTimeModule::class, PreferencesModule::class,
    SignInModule::class, SleepModule::class, StatisticsModule::class, WidgetModule::class,
    HomeAssistedModule::class, DiaryAssistedModule::class, DetailsAssistedModule::class,
    AddAssistedModule::class, StatisticsAssistedModule::class, WorkerBindingModule::class,
    BackupAssistedModule::class
])
interface TestComponent : AppComponent {
    @Component.Builder
    interface Builder {
        @BindsInstance
        fun application(application: App): Builder

        fun build(): TestComponent
    }

    fun inject(app: TestApp)
}
