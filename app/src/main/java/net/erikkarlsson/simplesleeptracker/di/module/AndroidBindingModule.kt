package net.erikkarlsson.simplesleeptracker.di.module

import dagger.Module
import dagger.android.ContributesAndroidInjector
import net.erikkarlsson.simplesleeptracker.MainActivity
import net.erikkarlsson.simplesleeptracker.di.scope.ActivityScope
import net.erikkarlsson.simplesleeptracker.di.scope.BroadcastRecieverScope
import net.erikkarlsson.simplesleeptracker.di.scope.FragmentScope
import net.erikkarlsson.simplesleeptracker.features.add.AddSleepActivity
import net.erikkarlsson.simplesleeptracker.features.add.AddSleepFragment
import net.erikkarlsson.simplesleeptracker.features.appwidget.SleepAppWidgetProvider
import net.erikkarlsson.simplesleeptracker.features.details.DetailActivity
import net.erikkarlsson.simplesleeptracker.features.details.DetailFragment
import net.erikkarlsson.simplesleeptracker.features.diary.DiaryFragment
import net.erikkarlsson.simplesleeptracker.features.diary.DiaryModule
import net.erikkarlsson.simplesleeptracker.features.home.HomeFragment
import net.erikkarlsson.simplesleeptracker.features.statistics.StatisticsFragment
import net.erikkarlsson.simplesleeptracker.features.statistics.item.StatisticsItemFragment

@Module
abstract class AndroidBindingModule {

    @ActivityScope
    @ContributesAndroidInjector
    abstract fun bindsMainActivity(): MainActivity

    @ActivityScope
    @ContributesAndroidInjector
    abstract fun bindsDetailActivity(): DetailActivity

    @ActivityScope
    @ContributesAndroidInjector
    abstract fun bindsAddSleepActivity(): AddSleepActivity

    @FragmentScope
    @ContributesAndroidInjector
    abstract fun bindsStatisticsFragment(): StatisticsFragment

    @FragmentScope
    @ContributesAndroidInjector
    abstract fun bindsStatisticsItemFragment(): StatisticsItemFragment

    @FragmentScope
    @ContributesAndroidInjector
    abstract fun bindsHomeFragment(): HomeFragment

    @FragmentScope
    @ContributesAndroidInjector(modules = [DiaryModule::class])
    abstract fun bindsDiaryFragment(): DiaryFragment

    @FragmentScope
    @ContributesAndroidInjector
    abstract fun bindDetailFragment(): DetailFragment

    @FragmentScope
    @ContributesAndroidInjector
    abstract fun bindAddSleepFragment(): AddSleepFragment
    
    @BroadcastRecieverScope
    @ContributesAndroidInjector
    abstract fun bindsAppWidgetProvider(): SleepAppWidgetProvider
}
