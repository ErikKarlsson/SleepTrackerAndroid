package net.erikkarlsson.simplesleeptracker.di.module

import dagger.Module
import dagger.android.ContributesAndroidInjector
import net.erikkarlsson.simplesleeptracker.MainActivity
import net.erikkarlsson.simplesleeptracker.di.scope.ActivityScope
import net.erikkarlsson.simplesleeptracker.di.scope.BroadcastRecieverScope
import net.erikkarlsson.simplesleeptracker.di.scope.FragmentScope
import net.erikkarlsson.simplesleeptracker.di.scope.ServiceScope
import net.erikkarlsson.simplesleeptracker.feature.add.AddSleepActivity
import net.erikkarlsson.simplesleeptracker.feature.appwidget.SleepAppWidgetProvider
import net.erikkarlsson.simplesleeptracker.feature.details.DetailActivity
import net.erikkarlsson.simplesleeptracker.feature.diary.DiaryFragment
import net.erikkarlsson.simplesleeptracker.feature.diary.DiaryModule
import net.erikkarlsson.simplesleeptracker.feature.home.HomeFragment
import net.erikkarlsson.simplesleeptracker.feature.settings.SettingsFragment
import net.erikkarlsson.simplesleeptracker.feature.sleepdetection.AlarmReceiver
import net.erikkarlsson.simplesleeptracker.feature.sleepdetection.SleepService
import net.erikkarlsson.simplesleeptracker.feature.statistics.StatisticsFragment
import net.erikkarlsson.simplesleeptracker.feature.statistics.item.StatisticsItemFragment

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
    @ContributesAndroidInjector
    abstract fun bindsSettingsFragment(): SettingsFragment

    @FragmentScope
    @ContributesAndroidInjector(modules = [DiaryModule::class])
    abstract fun bindsDiaryFragment(): DiaryFragment
    
    @BroadcastRecieverScope
    @ContributesAndroidInjector
    abstract fun bindsAppWidgetProvider(): SleepAppWidgetProvider

    @BroadcastRecieverScope
    @ContributesAndroidInjector
    abstract fun bindsAlarmReceiver(): AlarmReceiver

    @ServiceScope
    @ContributesAndroidInjector
    abstract fun bindsSleepService(): SleepService
}
