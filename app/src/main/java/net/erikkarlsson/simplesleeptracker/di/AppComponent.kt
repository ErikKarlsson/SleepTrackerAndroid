package net.erikkarlsson.simplesleeptracker.di

import dagger.BindsInstance
import dagger.Component
import dagger.android.AndroidInjectionModule
import net.erikkarlsson.simplesleeptracker.App
import net.erikkarlsson.simplesleeptracker.di.module.*
import javax.inject.Singleton

@Singleton
@Component(modules = [AndroidBindingModule::class, AndroidInjectionModule::class,
AppModule::class, DateTimeModule::class, SleepModule::class, ViewModelModule::class])

interface AppComponent {
    @Component.Builder
    interface Builder {
        @BindsInstance
        fun application(application: App): Builder

        fun build(): AppComponent
    }

    fun inject(app: App)
}