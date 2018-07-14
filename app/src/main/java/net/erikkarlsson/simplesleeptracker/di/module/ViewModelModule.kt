package net.erikkarlsson.simplesleeptracker.di.module

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import net.erikkarlsson.simplesleeptracker.MainViewModel
import net.erikkarlsson.simplesleeptracker.di.ViewModelFactory
import net.erikkarlsson.simplesleeptracker.di.ViewModelKey
import net.erikkarlsson.simplesleeptracker.feature.details.DetailViewModel
import net.erikkarlsson.simplesleeptracker.feature.diary.DiaryViewModel
import net.erikkarlsson.simplesleeptracker.feature.profile.ProfileViewModel
import net.erikkarlsson.simplesleeptracker.feature.statistics.StatisticsViewModel

@Module
abstract class ViewModelModule {

    @Binds
    @IntoMap
    @ViewModelKey(MainViewModel::class)
    abstract fun bindMainViewModel(statisticsViewModel: MainViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(StatisticsViewModel::class)
    abstract fun bindStatisticsViewModel(statisticsViewModel: StatisticsViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(DiaryViewModel::class)
    abstract fun bindDiaryViewModel(diaryViewModel: DiaryViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(DetailViewModel::class)
    abstract fun bindDetailViewModel(detailViewModel: DetailViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(ProfileViewModel::class)
    abstract fun bindProfileViewModel(profileViewModel: ProfileViewModel): ViewModel

    @Binds
    abstract fun bindViewModelFactory(factory: ViewModelFactory): ViewModelProvider.Factory

}
