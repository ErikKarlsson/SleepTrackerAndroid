package net.erikkarlsson.simplesleeptracker.features.backup

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import dagger.hilt.android.qualifiers.ApplicationContext
import net.erikkarlsson.simplesleeptracker.domain.task.CoroutineTask
import net.erikkarlsson.simplesleeptracker.features.backup.domain.BackupSleepTask
import timber.log.Timber

@HiltWorker
class BackupSleepWorker @AssistedInject constructor(
        @Assisted val context: Context,
        @Assisted val workerParams: WorkerParameters,
        private val backupSleepTask: BackupSleepTask
)
    : CoroutineWorker(context, workerParams) {

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
