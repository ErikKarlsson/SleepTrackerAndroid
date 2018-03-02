package net.erikkarlsson.simplesleeptracker.di.module

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import net.erikkarlsson.simplesleeptracker.di.ViewModelFactory
import net.erikkarlsson.simplesleeptracker.di.ViewModelKey
import net.erikkarlsson.simplesleeptracker.statistics.StatisticsViewModel
import net.erikkarlsson.simplesleeptracker.statisticselm.StatisticsElmViewModel

@Module
abstract class ViewModelModule {

    @Binds
    @IntoMap
    @ViewModelKey(StatisticsViewModel::class)
    abstract fun bindStatisticsViewModel(statisticsViewModel: StatisticsViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(StatisticsElmViewModel::class)
    abstract fun bindStatisticsElmViewModel(statisticsElmViewModel: StatisticsElmViewModel): ViewModel

    @Binds
    abstract fun bindViewModelFactory(factory: ViewModelFactory): ViewModelProvider.Factory

}
