package net.erikkarlsson.simplesleeptracker.features.backup

import androidx.work.*
import net.erikkarlsson.simplesleeptracker.domain.task.TaskScheduler
import javax.inject.Inject

const val TAG_BACKUP = "tag_backup"

class BackupScheduler @Inject constructor()
    : TaskScheduler {

    override fun schedule() {
        val constraints = Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build()

        val backupSleepDataRequest =
                OneTimeWorkRequest.Builder(BackupSleepWorker::class.java)
                        .setConstraints(constraints)
                        .build()

        WorkManager.getInstance().beginUniqueWork(TAG_BACKUP,
                ExistingWorkPolicy.KEEP,
                backupSleepDataRequest).enqueue()
    }
}
