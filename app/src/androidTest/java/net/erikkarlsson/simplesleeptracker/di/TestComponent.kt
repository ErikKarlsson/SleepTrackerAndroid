package net.erikkarlsson.simplesleeptracker.di

import dagger.BindsInstance
import dagger.Component
import dagger.android.AndroidInjectionModule
import net.erikkarlsson.simplesleeptracker.App
import net.erikkarlsson.simplesleeptracker.TestApp
import net.erikkarlsson.simplesleeptracker.di.module.*
import javax.inject.Singleton

@Singleton
@Component(modules = [AndroidBindingModule::class, AndroidInjectionModule::class,
    AppModule::class, BackupModule::class, EventModule::class,
    MockDateTimeModule::class, PreferencesModule::class, SignInModule::class,
    SleepModule::class, StatisticsModule::class, ViewModelModule::class, WidgetModule::class])
interface TestComponent : AppComponent {
    @Component.Builder
    interface Builder {
        @BindsInstance
        fun application(application: App): Builder

        fun build(): TestComponent
    }

    fun inject(app: TestApp)
}
