package net.erikkarlsson.simplesleeptracker.features.backup.domain

import kotlinx.coroutines.flow.first
import net.erikkarlsson.simplesleeptracker.domain.BackupCsvFileWriter
import net.erikkarlsson.simplesleeptracker.domain.FileBackupDataSource
import net.erikkarlsson.simplesleeptracker.domain.SleepDataSource
import net.erikkarlsson.simplesleeptracker.domain.task.CoroutineTask
import javax.inject.Inject

/**
 * Backup sleep to file.
 */
class BackupSleepTask @Inject constructor(
        private val sleepRepository: SleepDataSource,
        private val backupCsvFileWriter: BackupCsvFileWriter,
        private val fileBackupRepository: FileBackupDataSource) : CoroutineTask<CoroutineTask.None> {

    override suspend fun completable(params: CoroutineTask.None) {
        val sleepList = sleepRepository.getSleepListFlow().first()
        val file = backupCsvFileWriter.write(sleepList)
        fileBackupRepository.put(file)
        fileBackupRepository.updateLastBackupTimestamp()
    }
}
