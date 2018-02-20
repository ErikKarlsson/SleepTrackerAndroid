package net.erikkarlsson.simplesleeptracker.di

import dagger.Module
import dagger.android.ContributesAndroidInjector
import net.erikkarlsson.simplesleeptracker.sleepappwidget.SleepAppWidgetProvider
import net.erikkarlsson.simplesleeptracker.MainActivity
import net.erikkarlsson.simplesleeptracker.di.scope.ActivityScope
import net.erikkarlsson.simplesleeptracker.di.scope.BroadcastRecieverScope

@Module
abstract class AndroidBindingModule {

    @ActivityScope
    @ContributesAndroidInjector
    abstract fun bindMainActivity(): MainActivity

    @BroadcastRecieverScope
    @ContributesAndroidInjector
    abstract fun bindAppWidgetProvider(): SleepAppWidgetProvider
}