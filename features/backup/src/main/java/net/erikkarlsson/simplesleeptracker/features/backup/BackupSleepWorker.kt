package net.erikkarlsson.simplesleeptracker.features.backup

import android.content.Context
import androidx.hilt.Assisted
import androidx.hilt.work.WorkerInject
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import dagger.hilt.android.qualifiers.ApplicationContext
import net.erikkarlsson.simplesleeptracker.domain.task.CoroutineTask
import net.erikkarlsson.simplesleeptracker.features.backup.domain.BackupSleepTask
import timber.log.Timber

class BackupSleepWorker @WorkerInject constructor(
        @Assisted private val workerParams: WorkerParameters,
        @Assisted @ApplicationContext private val appContext: Context,
        private val backupSleepTask: BackupSleepTask
)
    : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        Timber.d("Backup data to Google Drive")

        return try {
            backupSleepTask.completable(CoroutineTask.None())
            Result.success()
        } catch (error: Throwable) {
            Result.failure()
        }
    }
}
