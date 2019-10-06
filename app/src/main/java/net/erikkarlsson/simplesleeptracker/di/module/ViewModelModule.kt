package net.erikkarlsson.simplesleeptracker.di.module

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import net.erikkarlsson.simplesleeptracker.MainViewModel
import net.erikkarlsson.simplesleeptracker.di.ViewModelFactory
import net.erikkarlsson.simplesleeptracker.di.ViewModelKey
import net.erikkarlsson.simplesleeptracker.feature.add.AddSleepViewModel
import net.erikkarlsson.simplesleeptracker.feature.diary.DiaryViewModel
import net.erikkarlsson.simplesleeptracker.feature.home.HomeViewModel
import net.erikkarlsson.simplesleeptracker.feature.statistics.StatisticsViewModel
import net.erikkarlsson.simplesleeptracker.feature.statistics.item.StatisticsItemViewModel

@Module
abstract class ViewModelModule {

    @Binds
    @IntoMap
    @ViewModelKey(MainViewModel::class)
    abstract fun bindMainViewModel(statisticsViewModel: MainViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(StatisticsItemViewModel::class)
    abstract fun bindStatisticsItemViewModel(statisticsItemViewModel: StatisticsItemViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(DiaryViewModel::class)
    abstract fun bindDiaryViewModel(diaryViewModel: DiaryViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(AddSleepViewModel::class)
    abstract fun bindAddSleepViewModel(addSleepViewModel: AddSleepViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(HomeViewModel::class)
    abstract fun bindHomeViewModel(homeViewModel: HomeViewModel): ViewModel

    @Binds
    abstract fun bindViewModelFactory(factory: ViewModelFactory): ViewModelProvider.Factory

}
