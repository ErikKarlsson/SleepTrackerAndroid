package net.erikkarlsson.simplesleeptracker.features.diary.di

import com.squareup.inject.assisted.dagger2.AssistedModule
import dagger.Module

@AssistedModule
@Module(includes = [AssistedInject_DiaryAssistedModule::class])
abstract class DiaryAssistedModule
