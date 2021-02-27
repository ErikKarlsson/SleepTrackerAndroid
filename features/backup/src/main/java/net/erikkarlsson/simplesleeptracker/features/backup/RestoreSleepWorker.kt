package net.erikkarlsson.simplesleeptracker.features.backup

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import dagger.hilt.android.qualifiers.ApplicationContext
import net.erikkarlsson.simplesleeptracker.domain.task.CoroutineTask
import net.erikkarlsson.simplesleeptracker.features.backup.domain.RestoreSleepBackupTask
import timber.log.Timber

@HiltWorker
class RestoreSleepWorker @AssistedInject constructor(
        @Assisted val context: Context,
        @Assisted val workerParams: WorkerParameters,
        private val restoreSleepBackupTask: RestoreSleepBackupTask
)
    : CoroutineWorker(context, workerParams) {

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
