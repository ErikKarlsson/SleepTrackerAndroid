package net.erikkarlsson.simplesleeptracker.features.backup

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.squareup.inject.assisted.Assisted
import com.squareup.inject.assisted.AssistedInject
import net.erikkarlsson.simplesleeptracker.domain.task.CompletableTask
import net.erikkarlsson.simplesleeptracker.features.backup.domain.BackupSleepTask
import timber.log.Timber

class BackupSleepWorker @AssistedInject constructor(
        @Assisted private val appContext : Context,
        @Assisted private val params : WorkerParameters,
        private val backupSleepTask: BackupSleepTask)
    : Worker(appContext, params) {

    override fun doWork(): Result {
        Timber.d("Backup data to Google Drive")

        val throwable = backupSleepTask.completable(CompletableTask.None())
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
