package net.erikkarlsson.simplesleeptracker.features.details.di

import com.squareup.inject.assisted.dagger2.AssistedModule
import dagger.Module

@AssistedModule
@Module(includes = [AssistedInject_DetailsAssistedModule::class])
abstract class DetailsAssistedModule