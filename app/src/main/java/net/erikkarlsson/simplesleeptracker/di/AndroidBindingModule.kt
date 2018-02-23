package net.erikkarlsson.simplesleeptracker.di

import dagger.Module
import dagger.android.ContributesAndroidInjector
import net.erikkarlsson.simplesleeptracker.MainActivity
import net.erikkarlsson.simplesleeptracker.di.scope.ActivityScope
import net.erikkarlsson.simplesleeptracker.di.scope.BroadcastRecieverScope
import net.erikkarlsson.simplesleeptracker.sleepappwidget.SleepAppWidgetProvider

@Module
abstract class AndroidBindingModule {

    @ActivityScope
    @ContributesAndroidInjector
    abstract fun bindMainActivity(): MainActivity

    @BroadcastRecieverScope
    @ContributesAndroidInjector(modules = [AppWidgetModule::class])
    abstract fun bindAppWidgetProvider(): SleepAppWidgetProvider
}