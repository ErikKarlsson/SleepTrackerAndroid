package net.erikkarlsson.simplesleeptracker.domain.task

import io.reactivex.Completable
import javax.inject.Inject
import javax.inject.Named

class ScheduleBackupTask @Inject constructor(
        @Named("backupScheduler") private val backupScheduler: TaskScheduler)
    : CompletableTask<CompletableTask.None> {
    override fun completable(params: CompletableTask.None): Completable {
        return Completable.fromCallable {
            backupScheduler.schedule()
        }
    }

}
