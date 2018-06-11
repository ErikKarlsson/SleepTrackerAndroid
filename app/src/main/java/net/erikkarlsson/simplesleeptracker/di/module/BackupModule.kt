package net.erikkarlsson.simplesleeptracker.di.module

import dagger.Binds
import dagger.Module
import net.erikkarlsson.simplesleeptracker.data.DriveFileBackupRepository
import net.erikkarlsson.simplesleeptracker.data.FastSleepToCsvFile
import net.erikkarlsson.simplesleeptracker.domain.FileBackupDataSource
import net.erikkarlsson.simplesleeptracker.domain.SleepToCsvFile
import net.erikkarlsson.simplesleeptracker.domain.task.TaskScheduler
import net.erikkarlsson.simplesleeptracker.feature.backup.PeriodicBackupScheduler
import net.erikkarlsson.simplesleeptracker.feature.backup.RestoreBackupScheduler
import javax.inject.Named
import javax.inject.Singleton

@Module
abstract class BackupModule() {

    @Binds
    @Singleton
    abstract fun bindsSleepToCsvFile(fastSleepToCsvFile: FastSleepToCsvFile): SleepToCsvFile

    @Binds
    @Singleton
    abstract fun bindsFileBackupDataSource(driveFileBackupRepository: DriveFileBackupRepository): FileBackupDataSource

    @Binds
    @Singleton
    @Named("periodicBackupScheduler")
    abstract fun bindsPeriodicBackupScheduler(periodicBackupScheduler: PeriodicBackupScheduler): TaskScheduler

    @Binds
    @Singleton
    @Named("restoreBackupScheduler")
    abstract fun bindsRestoreBackupScheduler(restBackupScheduler: RestoreBackupScheduler): TaskScheduler

}
