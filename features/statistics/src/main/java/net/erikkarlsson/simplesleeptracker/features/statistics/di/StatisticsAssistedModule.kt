package net.erikkarlsson.simplesleeptracker.features.statistics.di

import com.squareup.inject.assisted.dagger2.AssistedModule
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.FragmentComponent
import dagger.hilt.migration.DisableInstallInCheck

@AssistedModule
@Module
@InstallIn(FragmentComponent::class)
abstract class StatisticsAssistedModule
