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

@Module
abstract class ViewModelModule {

    @Binds
    @IntoMap
    @ViewModelKey(MainViewModel::class)
    abstract fun bindMainViewModel(statisticsViewModel: MainViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(AddSleepViewModel::class)
    abstract fun bindAddSleepViewModel(addSleepViewModel: AddSleepViewModel): ViewModel

    @Binds
    abstract fun bindViewModelFactory(factory: ViewModelFactory): ViewModelProvider.Factory

}
