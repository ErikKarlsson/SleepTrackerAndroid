package net.erikkarlsson.simplesleeptracker.features.details.di

import com.squareup.inject.assisted.dagger2.AssistedModule
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import dagger.hilt.migration.DisableInstallInCheck

@AssistedModule
@Module(includes = [AssistedInject_DetailsAssistedModule::class])
@DisableInstallInCheck
@InstallIn(ApplicationComponent::class)
abstract class DetailsAssistedModule
