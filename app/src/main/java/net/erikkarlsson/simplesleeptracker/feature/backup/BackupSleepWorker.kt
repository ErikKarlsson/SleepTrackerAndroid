package net.erikkarlsson.simplesleeptracker.feature.backup

import androidx.work.Worker
import net.erikkarlsson.simplesleeptracker.App
import net.erikkarlsson.simplesleeptracker.domain.task.backup.BackupSleepTask
import net.erikkarlsson.simplesleeptracker.domain.task.CompletableTask
import timber.log.Timber
import javax.inject.Inject

class BackupSleepWorker: Worker() {

    @Inject lateinit var backupSleepTask: BackupSleepTask

    override fun doWork(): WorkerResult {
        Timber.d("Backup data to Google Drive")

        // TODO (erikkarlsson): Replace with Dagger Android inject when supported.
        (applicationContext as App).appComponent.inject(this)

        val throwable = backupSleepTask.execute(CompletableTask.None())
                .blockingGet()

        return if (throwable == null) {
            Worker.WorkerResult.SUCCESS
        } else {
            Worker.WorkerResult.FAILURE
        }
    }
}