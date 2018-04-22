package net.erikkarlsson.simplesleeptracker.di.module

import dagger.Module
import dagger.android.ContributesAndroidInjector
import net.erikkarlsson.simplesleeptracker.appwidget.SleepAppWidgetProvider
import net.erikkarlsson.simplesleeptracker.details.DetailActivity
import net.erikkarlsson.simplesleeptracker.di.scope.ActivityScope
import net.erikkarlsson.simplesleeptracker.di.scope.BroadcastRecieverScope
import net.erikkarlsson.simplesleeptracker.di.scope.FragmentScope
import net.erikkarlsson.simplesleeptracker.diary.DiaryFragment
import net.erikkarlsson.simplesleeptracker.statistics.StatisticsFragment

@Module
abstract class AndroidBindingModule {

    @ActivityScope
    @ContributesAndroidInjector
    abstract fun bindDetailActivity(): DetailActivity

    @FragmentScope
    @ContributesAndroidInjector
    abstract fun bindsStatisticsFragment(): StatisticsFragment

    @FragmentScope
    @ContributesAndroidInjector
    abstract fun bindsDiaryFragment(): DiaryFragment
    
    @BroadcastRecieverScope
    @ContributesAndroidInjector
    abstract fun bindAppWidgetProvider(): SleepAppWidgetProvider
}