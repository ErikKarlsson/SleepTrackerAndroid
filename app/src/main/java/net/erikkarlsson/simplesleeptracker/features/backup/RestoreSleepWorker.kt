package net.erikkarlsson.simplesleeptracker.features.backup

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters
import net.erikkarlsson.simplesleeptracker.App
import net.erikkarlsson.simplesleeptracker.domain.task.CompletableTask
import net.erikkarlsson.simplesleeptracker.features.backup.domain.RestoreSleepBackupTask
import timber.log.Timber
import javax.inject.Inject

class RestoreSleepWorker constructor(context : Context, params : WorkerParameters)
    : Worker(context, params) {

    @Inject lateinit var restoreSleepBackupTask: RestoreSleepBackupTask

    override fun doWork(): Result {
        Timber.d("Restore data from Google Drive")

        // TODO (erikkarlsson): Replace with Dagger Android inject when supported.
        (applicationContext as App).appComponent.inject(this)

        val throwable = restoreSleepBackupTask.completable(CompletableTask.None())
                .blockingGet()

        return if (throwable == null) {
            Result.success()
        } else {
            Result.failure()
        }
    }
}
