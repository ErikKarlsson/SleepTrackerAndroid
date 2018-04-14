package net.erikkarlsson.simplesleeptracker.di.module

import dagger.Module
import dagger.android.ContributesAndroidInjector
import net.erikkarlsson.simplesleeptracker.appwidget.SleepAppWidgetProvider
import net.erikkarlsson.simplesleeptracker.details.DetailActivity
import net.erikkarlsson.simplesleeptracker.di.scope.ActivityScope
import net.erikkarlsson.simplesleeptracker.di.scope.BroadcastRecieverScope
import net.erikkarlsson.simplesleeptracker.di.scope.FragmentScope
import net.erikkarlsson.simplesleeptracker.statistics.StatisticsFragment

@Module
abstract class AndroidBindingModule {

    @ActivityScope
    @ContributesAndroidInjector
    abstract fun bindDetailsActivity(): DetailActivity

    @FragmentScope
    @ContributesAndroidInjector
    abstract fun bindsStatisticsFragment(): StatisticsFragment
    
    @BroadcastRecieverScope
    @ContributesAndroidInjector
    abstract fun bindAppWidgetProvider(): SleepAppWidgetProvider
}