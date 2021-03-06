package net.erikkarlsson.simplesleeptracker.features.backup.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import net.erikkarlsson.simplesleeptracker.domain.BackupCsvFileReader
import net.erikkarlsson.simplesleeptracker.domain.BackupCsvFileWriter
import net.erikkarlsson.simplesleeptracker.domain.FileBackupDataSource
import net.erikkarlsson.simplesleeptracker.domain.task.TaskScheduler
import net.erikkarlsson.simplesleeptracker.features.backup.BackupScheduler
import net.erikkarlsson.simplesleeptracker.features.backup.FastBackupCsvFileReader
import net.erikkarlsson.simplesleeptracker.features.backup.FastBackupCsvFileWriter
import net.erikkarlsson.simplesleeptracker.features.backup.RestoreBackupScheduler
import net.erikkarlsson.simplesleeptracker.features.backup.data.DriveFileBackupRepository
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class BackupModule {

    @Binds
    @Singleton
    abstract fun bindsFileBackupDataSource(driveFileBackupRepository: DriveFileBackupRepository): FileBackupDataSource

    @Binds
    @Singleton
    abstract fun bindsBackupCsvFileReader(fastBackupCsvFileReader: FastBackupCsvFileReader): BackupCsvFileReader

    @Binds
    @Singleton
    abstract fun bindsBackupCsvFileWriter(fastBackupCsvFileWriter: FastBackupCsvFileWriter): BackupCsvFileWriter

    @Binds
    @Singleton
    @Named("backupScheduler")
    abstract fun bindsBackupScheduler(backupScheduler: BackupScheduler): TaskScheduler

    @Binds
    @Singleton
    @Named("restoreBackupScheduler")
    abstract fun bindsRestoreBackupScheduler(restBackupScheduler: RestoreBackupScheduler): TaskScheduler

}
