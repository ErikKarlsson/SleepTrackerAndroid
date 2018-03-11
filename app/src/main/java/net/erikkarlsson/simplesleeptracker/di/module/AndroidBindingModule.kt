package net.erikkarlsson.simplesleeptracker.di.module

import dagger.Module
import dagger.android.ContributesAndroidInjector
import net.erikkarlsson.simplesleeptracker.MainActivity
import net.erikkarlsson.simplesleeptracker.details.DetailsActivity
import net.erikkarlsson.simplesleeptracker.di.scope.ActivityScope
import net.erikkarlsson.simplesleeptracker.di.scope.BroadcastRecieverScope
import net.erikkarlsson.simplesleeptracker.sleepappwidget.SleepAppWidgetProvider
import net.erikkarlsson.simplesleeptracker.statistics.StatisticsActivity

@Module
abstract class AndroidBindingModule {

    @ActivityScope
    @ContributesAndroidInjector
    abstract fun bindMainActivity(): MainActivity

    @ActivityScope
    @ContributesAndroidInjector
    abstract fun bindStatisticsActivity(): StatisticsActivity

    @ActivityScope
    @ContributesAndroidInjector
    abstract fun bindDetailsActivity(): DetailsActivity

    @BroadcastRecieverScope
    @ContributesAndroidInjector(modules = [AppWidgetModule::class])
    abstract fun bindAppWidgetProvider(): SleepAppWidgetProvider
}