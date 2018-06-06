package net.erikkarlsson.simplesleeptracker.di.module

import dagger.Binds
import dagger.Module
import net.erikkarlsson.simplesleeptracker.data.DriveFileBackupRepository
import net.erikkarlsson.simplesleeptracker.data.FastSleepToCsvFile
import net.erikkarlsson.simplesleeptracker.domain.FileBackupDataSource
import net.erikkarlsson.simplesleeptracker.domain.SleepToCsvFile
import javax.inject.Singleton

@Module
abstract class BackupModule() {

    @Binds
    @Singleton
    abstract fun bindsSleepToCsvFile(fastSleepToCsvFile: FastSleepToCsvFile): SleepToCsvFile

    @Binds
    @Singleton
    abstract fun bindsFileBackupDataSource(driveFileBackupRepository: DriveFileBackupRepository): FileBackupDataSource

/*
    @Module
    companion object {

        @JvmStatic
        @Provides
        fun provides

    }
*/

}
