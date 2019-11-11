package net.erikkarlsson.simplesleeptracker.features.backup

import androidx.work.*
import net.erikkarlsson.simplesleeptracker.domain.task.TaskScheduler
import javax.inject.Inject

const val TAG_RESTORE_BACKUP = "tag_restore_backup"

class RestoreBackupScheduler @Inject constructor(private val workManager: WorkManager)
    : TaskScheduler {

    override fun schedule() {
        val constraints = Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build()

        val restoreSleepDataRequest =
                OneTimeWorkRequest.Builder(RestoreSleepWorker::class.java)
                        .setConstraints(constraints)
                        .build()

        workManager.beginUniqueWork(TAG_RESTORE_BACKUP,
                ExistingWorkPolicy.KEEP,
                restoreSleepDataRequest).enqueue()
    }
}
