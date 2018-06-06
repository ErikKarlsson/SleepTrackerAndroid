package net.erikkarlsson.simplesleeptracker.di

import dagger.BindsInstance
import dagger.Component
import dagger.android.AndroidInjectionModule
import net.erikkarlsson.simplesleeptracker.App
import net.erikkarlsson.simplesleeptracker.TestApp
import net.erikkarlsson.simplesleeptracker.di.module.AndroidBindingModule
import net.erikkarlsson.simplesleeptracker.di.module.AppModule
import net.erikkarlsson.simplesleeptracker.di.module.BackupModule
import net.erikkarlsson.simplesleeptracker.di.module.SignInModule
import net.erikkarlsson.simplesleeptracker.di.module.SleepModule
import net.erikkarlsson.simplesleeptracker.di.module.ViewModelModule
import javax.inject.Singleton

@Singleton
@Component(modules = [AndroidBindingModule::class, AndroidInjectionModule::class,
    AppModule::class, BackupModule::class, MockDateTimeModule::class, SignInModule::class,
    SleepModule::class, ViewModelModule::class])
interface TestComponent : AppComponent {
    @Component.Builder
    interface Builder {
        @BindsInstance
        fun application(application: App): Builder

        fun build(): TestComponent
    }

    fun inject(app: TestApp)
}