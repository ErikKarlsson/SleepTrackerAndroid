package net.erikkarlsson.simplesleeptracker.di

import dagger.BindsInstance
import dagger.Component
import dagger.android.AndroidInjectionModule
import net.erikkarlsson.simplesleeptracker.App
import net.erikkarlsson.simplesleeptracker.TestApp
import net.erikkarlsson.simplesleeptracker.di.module.*
import net.erikkarlsson.simplesleeptracker.features.diary.di.DiaryAssistedModule
import net.erikkarlsson.simplesleeptracker.features.home.di.module.HomeAssistedModule
import javax.inject.Singleton

@Singleton
@Component(modules = [AndroidBindingModule::class, AndroidInjectionModule::class,
    AppAssistedModule::class, AppModule::class, BackupModule::class, EventModule::class,
    NotificationModule::class, MockDateTimeModule::class, PreferencesModule::class,
    SignInModule::class, SleepModule::class, StatisticsModule::class, WidgetModule::class,
    HomeAssistedModule::class, DiaryAssistedModule::class
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
