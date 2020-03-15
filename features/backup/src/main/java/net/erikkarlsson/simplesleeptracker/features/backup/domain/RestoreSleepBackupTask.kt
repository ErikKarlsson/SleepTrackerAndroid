package net.erikkarlsson.simplesleeptracker.features.backup.domain

import kotlinx.coroutines.flow.first
import net.erikkarlsson.simplesleeptracker.domain.BackupCsvFileReader
import net.erikkarlsson.simplesleeptracker.domain.FileBackupDataSourceCoroutines
import net.erikkarlsson.simplesleeptracker.domain.SleepDataSourceCoroutines
import net.erikkarlsson.simplesleeptracker.domain.task.CoroutineTask
import javax.inject.Inject

/**
 * Restore sleep data from backup file.
 *
 * Requires that user hasn't already created any sleep records.
 */
class RestoreSleepBackupTask @Inject constructor(
        private val backupCsvFileReader: BackupCsvFileReader,
        private val sleepRepository: SleepDataSourceCoroutines,
        private val fileBackupRepository: FileBackupDataSourceCoroutines) : CoroutineTask<CoroutineTask.None> {

    override suspend fun completable(params: CoroutineTask.None) {
        val count = sleepRepository.getCountFlow().first()

        if (count == 0) {
            restoreBackup()
        }
    }

    private suspend fun restoreBackup() {
        val file = fileBackupRepository.get()

        file?.let {
            val sleepList = backupCsvFileReader.read(it)
            sleepRepository.insertAll(sleepList)
            fileBackupRepository.updateLastBackupTimestamp()
        }
    }
}
