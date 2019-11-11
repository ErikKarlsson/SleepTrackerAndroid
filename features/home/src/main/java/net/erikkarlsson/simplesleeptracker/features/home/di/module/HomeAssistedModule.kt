package net.erikkarlsson.simplesleeptracker.features.home.di.module

import com.squareup.inject.assisted.dagger2.AssistedModule
import dagger.Module

@AssistedModule
@Module(includes = [AssistedInject_HomeAssistedModule::class])
abstract class HomeAssistedModule
