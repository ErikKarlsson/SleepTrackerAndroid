package net.erikkarlsson.simplesleeptracker.features.backup

import android.content.Context
import androidx.work.*
import dagger.hilt.android.qualifiers.ApplicationContext
import net.erikkarlsson.simplesleeptracker.domain.task.TaskScheduler
import javax.inject.Inject

const val TAG_BACKUP = "tag_backup"

class BackupScheduler @Inject constructor(@ApplicationContext private val context: Context)
    : TaskScheduler {

    override fun schedule() {

        val constraints = Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build()

        val backupSleepDataRequest =
                OneTimeWorkRequest.Builder(BackupSleepWorker::class.java)
                        .setConstraints(constraints)
                        .build()

        WorkManager.getInstance(context).beginUniqueWork(TAG_BACKUP,
                ExistingWorkPolicy.KEEP,
                backupSleepDataRequest).enqueue()
    }
}
