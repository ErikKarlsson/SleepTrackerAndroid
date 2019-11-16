package net.erikkarlsson.simplesleeptracker.features.statistics.di

import com.squareup.inject.assisted.dagger2.AssistedModule
import dagger.Module

@AssistedModule
@Module(includes = [AssistedInject_StatisticsAssistedModule::class])
abstract class StatisticsAssistedModule
