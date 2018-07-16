package net.erikkarlsson.simplesleeptracker.feature.backup.domain

import io.reactivex.Completable
import io.reactivex.schedulers.Schedulers
import net.erikkarlsson.simplesleeptracker.domain.BackupCsvFileReader
import net.erikkarlsson.simplesleeptracker.domain.FileBackupDataSource
import net.erikkarlsson.simplesleeptracker.domain.SleepDataSource
import net.erikkarlsson.simplesleeptracker.domain.task.CompletableTask
import net.erikkarlsson.simplesleeptracker.domain.task.CompletableTask.None
import javax.inject.Inject

/**
 * Restore sleep data from backup file.
 *
 * Requires that user hasn't already created any sleep records.
 */
class RestoreSleepBackupTask @Inject constructor(
        private val backupCsvFileReader: BackupCsvFileReader,
        private val sleepRepository: SleepDataSource,
        private val fileBackupRepository: FileBackupDataSource) : CompletableTask<None> {

    override fun execute(params: None): Completable =
            sleepRepository.getCount()
                    .take(1)
                    .filter { it == 0 }
                    .flatMapCompletable { restoreBackup() }

    private fun restoreBackup(): Completable =
            fileBackupRepository.get()
                    .subscribeOn(Schedulers.io())
                    .map(backupCsvFileReader::read)
                    .flatMapCompletable {
                        sleepRepository.insertAll(it)
                                .subscribeOn(Schedulers.io())
                    }
}