package net.erikkarlsson.simplesleeptracker.feature.backup

import androidx.work.Constraints
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequest
import androidx.work.WorkManager
import androidx.work.WorkStatus
import java.util.concurrent.TimeUnit
import javax.inject.Inject

const val TAG_DAILY_BACKUP = "tag_daily_backup"
const val REPEAT_INTERVAL_DAYS = 1L

class PeriodicBackupScheduler @Inject constructor(private val workManager: WorkManager) {

    init {
        workManager.getStatusesByTag(TAG_DAILY_BACKUP)
                .observeForever(this::onWorkStatus)
    }

    private fun onWorkStatus(it: MutableList<WorkStatus>?) {
        if (it == null || it.isEmpty()) {
            schedulePeriodicBackup()
        }
    }

    private fun schedulePeriodicBackup() {
        val constraints = Constraints.Builder()
                .setRequiresCharging(true)
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build()

        val backupSleepData =
                PeriodicWorkRequest.Builder(BackupSleepWorker::class.java,
                                            REPEAT_INTERVAL_DAYS,
                                            TimeUnit.DAYS)
                        .setConstraints(constraints)
                        .addTag(TAG_DAILY_BACKUP)
                        .build()

        workManager.enqueue(backupSleepData)
    }

}