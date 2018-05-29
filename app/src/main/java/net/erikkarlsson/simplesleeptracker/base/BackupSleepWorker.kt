package net.erikkarlsson.simplesleeptracker.base

import androidx.work.Worker
import timber.log.Timber

class BackupSleepWorker: Worker() {

    override fun doWork(): WorkerResult {
        Timber.d("Backup data to Google Drive")
        return Worker.WorkerResult.SUCCESS
    }
}