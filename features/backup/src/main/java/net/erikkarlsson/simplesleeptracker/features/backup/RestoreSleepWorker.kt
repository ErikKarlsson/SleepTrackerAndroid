package net.erikkarlsson.simplesleeptracker.features.backup

import android.content.Context
import androidx.hilt.Assisted
import androidx.hilt.work.WorkerInject
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import dagger.hilt.android.qualifiers.ApplicationContext
import net.erikkarlsson.simplesleeptracker.domain.task.CoroutineTask
import net.erikkarlsson.simplesleeptracker.features.backup.domain.RestoreSleepBackupTask
import timber.log.Timber

class RestoreSleepWorker @WorkerInject constructor(
        @Assisted private val workerParams: WorkerParameters,
        @Assisted @ApplicationContext private val appContext: Context,
        private val restoreSleepBackupTask: RestoreSleepBackupTask
)
    : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        Timber.d("Restore data from Google Drive")

        return try {
            restoreSleepBackupTask.completable(CoroutineTask.None())
            Result.success()
        } catch (error: Throwable) {
            Result.failure()
        }
    }
}
