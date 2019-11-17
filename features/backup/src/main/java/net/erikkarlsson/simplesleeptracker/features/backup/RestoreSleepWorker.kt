package net.erikkarlsson.simplesleeptracker.features.backup

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.squareup.inject.assisted.Assisted
import com.squareup.inject.assisted.AssistedInject
import net.erikkarlsson.simplesleeptracker.domain.task.CompletableTask
import net.erikkarlsson.simplesleeptracker.features.backup.domain.RestoreSleepBackupTask
import timber.log.Timber

class RestoreSleepWorker @AssistedInject constructor(
        @Assisted private val appContext: Context,
        @Assisted private val params: WorkerParameters,
        private val restoreSleepBackupTask: RestoreSleepBackupTask)
    : Worker(appContext, params) {

    override fun doWork(): Result {
        Timber.d("Restore data from Google Drive")

        val throwable = restoreSleepBackupTask.completable(CompletableTask.None())
                .blockingGet()

        return if (throwable == null) {
            Result.success()
        } else {
            Result.failure()
        }
    }

    @AssistedInject.Factory
    interface Factory : ChildWorkerFactory
}
