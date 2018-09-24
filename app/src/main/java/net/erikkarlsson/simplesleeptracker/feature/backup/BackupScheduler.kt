package net.erikkarlsson.simplesleeptracker.feature.backup

import androidx.work.*
import net.erikkarlsson.simplesleeptracker.domain.task.TaskScheduler
import javax.inject.Inject

const val TAG_BACKUP = "tag_backup"

class BackupScheduler @Inject constructor(private val workManager: WorkManager)
    : TaskScheduler {

    override fun schedule() {
        val constraints = Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build()

        val backupSleepDataRequest =
                OneTimeWorkRequest.Builder(BackupSleepWorker::class.java)
                        .setConstraints(constraints)
                        .build()

        workManager.beginUniqueWork(TAG_BACKUP,
                ExistingWorkPolicy.KEEP,
                backupSleepDataRequest).enqueue()
    }
}
