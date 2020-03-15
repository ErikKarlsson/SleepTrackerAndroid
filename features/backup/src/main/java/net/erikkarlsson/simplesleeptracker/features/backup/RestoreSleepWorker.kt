package net.erikkarlsson.simplesleeptracker.features.backup

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.squareup.inject.assisted.Assisted
import com.squareup.inject.assisted.AssistedInject
import net.erikkarlsson.simplesleeptracker.domain.task.CoroutineTask
import net.erikkarlsson.simplesleeptracker.features.backup.domain.RestoreSleepBackupTask
import timber.log.Timber

class RestoreSleepWorker @AssistedInject constructor(
        @Assisted private val appContext: Context,
        @Assisted private val params: WorkerParameters,
        private val restoreSleepBackupTask: RestoreSleepBackupTask)
    : CoroutineWorker(appContext, params) {

    override suspend fun doWork(): Result {
        Timber.d("Restore data from Google Drive")

        return try {
            restoreSleepBackupTask.completable(CoroutineTask.None())
            Result.success()
        } catch (error: Throwable) {
            Result.failure()
        }
    }

    @AssistedInject.Factory
    interface Factory : ChildWorkerFactory
}
