package net.erikkarlsson.simplesleeptracker.feature.backup

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters
import net.erikkarlsson.simplesleeptracker.App
import net.erikkarlsson.simplesleeptracker.domain.task.CompletableTask
import net.erikkarlsson.simplesleeptracker.feature.backup.domain.BackupSleepTask
import timber.log.Timber
import javax.inject.Inject

class BackupSleepWorker constructor(context : Context, params : WorkerParameters)
    : Worker(context, params) {

    @Inject lateinit var backupSleepTask: BackupSleepTask

    override fun doWork(): Result {
        Timber.d("Backup data to Google Drive")

        // TODO (erikkarlsson): Replace with Dagger Android inject when supported.
        (applicationContext as App).appComponent.inject(this)

        val throwable = backupSleepTask.completable(CompletableTask.None())
                .blockingGet()

        return if (throwable == null) {
            Result.success()
        } else {
            Result.failure()
        }
    }
}
