package net.erikkarlsson.simplesleeptracker.feature.backup

import androidx.work.Worker
import net.erikkarlsson.simplesleeptracker.App
import net.erikkarlsson.simplesleeptracker.domain.task.CompletableTask
import net.erikkarlsson.simplesleeptracker.domain.task.backup.RestoreSleepBackupTask
import timber.log.Timber
import javax.inject.Inject

class RestoreSleepWorker: Worker() {

    @Inject lateinit var restoreSleepBackupTask: RestoreSleepBackupTask

    override fun doWork(): WorkerResult {
        Timber.d("Restore data from Google Drive")

        // TODO (erikkarlsson): Replace with Dagger Android inject when supported.
        (applicationContext as App).appComponent.inject(this)

        val throwable = restoreSleepBackupTask.execute(CompletableTask.None())
                .blockingGet()

        return if (throwable == null) {
            Worker.WorkerResult.SUCCESS
        } else {
            Worker.WorkerResult.FAILURE
        }
    }
}