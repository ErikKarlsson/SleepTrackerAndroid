package net.erikkarlsson.simplesleeptracker.di.module

import dagger.Binds
import dagger.Module
import net.erikkarlsson.simplesleeptracker.base.RxNetworkProvider
import net.erikkarlsson.simplesleeptracker.domain.NetworkProvider

@Module
abstract class NetworkModule {

    @Binds
    abstract fun bindNetworkProvider(rxNetworkProvider: RxNetworkProvider): NetworkProvider

}