package net.erikkarlsson.simplesleeptracker.features.backup.di

import com.squareup.inject.assisted.dagger2.AssistedModule
import dagger.Module

@AssistedModule
@Module(includes = [AssistedInject_BackupAssistedModule::class])
abstract class BackupAssistedModule
