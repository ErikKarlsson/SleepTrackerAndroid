package net.erikkarlsson.simplesleeptracker.feature.backup

import androidx.work.Constraints
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import net.erikkarlsson.simplesleeptracker.domain.task.TaskScheduler
import javax.inject.Inject

const val TAG_RESTORE_BACKUP = "tag_restore_backup"

class RestoreBackupScheduler @Inject constructor(private val workManager: WorkManager)
    : TaskScheduler {

    override fun schedule() {
        val constraints = Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build()

        val restoreSleepData = OneTimeWorkRequest.Builder(RestoreSleepWorker::class.java)
                .setConstraints(constraints)
                .build()

        workManager.beginUniqueWork(TAG_RESTORE_BACKUP,
                                    ExistingWorkPolicy.KEEP,
                                    restoreSleepData).enqueue()
    }
}
