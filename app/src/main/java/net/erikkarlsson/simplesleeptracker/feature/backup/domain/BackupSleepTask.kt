package net.erikkarlsson.simplesleeptracker.feature.backup.domain

import com.google.common.collect.ImmutableList
import io.reactivex.Completable
import net.erikkarlsson.simplesleeptracker.domain.BackupCsvFileWriter
import net.erikkarlsson.simplesleeptracker.domain.FileBackupDataSource
import net.erikkarlsson.simplesleeptracker.domain.SleepDataSource
import net.erikkarlsson.simplesleeptracker.domain.task.CompletableTask
import net.erikkarlsson.simplesleeptracker.domain.task.CompletableTask.None
import javax.inject.Inject

/**
 * Backup sleep to file.
 */
class BackupSleepTask @Inject constructor(
        private val sleepRepository: SleepDataSource,
        private val backupCsvFileWriter: BackupCsvFileWriter,
        private val fileBackupRepository: FileBackupDataSource) : CompletableTask<None> {

    override fun completable(params: None): Completable =
            sleepRepository.getSleep()
                    .first(ImmutableList.of())
                    .map(backupCsvFileWriter::write)
                    .flatMapCompletable(fileBackupRepository::put)
                    .andThen(fileBackupRepository.updateLastBackupTimestamp())
}
