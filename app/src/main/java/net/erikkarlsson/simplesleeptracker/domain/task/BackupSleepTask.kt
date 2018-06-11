package net.erikkarlsson.simplesleeptracker.domain.task

import com.google.common.collect.ImmutableList
import io.reactivex.Completable
import net.erikkarlsson.simplesleeptracker.domain.BackupCsvFileWriter
import net.erikkarlsson.simplesleeptracker.domain.FileBackupDataSource
import net.erikkarlsson.simplesleeptracker.domain.SleepDataSource
import net.erikkarlsson.simplesleeptracker.domain.task.CompletableTask.None
import javax.inject.Inject

/**
 * Backup sleep to file.
 *
 * Requires that user has created at least one sleep record.
 */
class BackupSleepTask @Inject constructor(
        private val sleepRepository: SleepDataSource,
        private val backupCsvFileWriter: BackupCsvFileWriter,
        private val fileBackupRepository: FileBackupDataSource) : CompletableTask<None> {

    override fun execute(params: None): Completable =
            sleepRepository.getSleep()
                    .first(ImmutableList.of())
                    .filter { it.size > 0 }
                    .map(backupCsvFileWriter::write)
                    .flatMapCompletable(fileBackupRepository::put)
}
