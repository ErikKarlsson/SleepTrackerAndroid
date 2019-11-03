package net.erikkarlsson.simplesleeptracker.feature.backup.domain

import io.reactivex.Completable
import net.erikkarlsson.simplesleeptracker.domain.task.CompletableTask
import net.erikkarlsson.simplesleeptracker.domain.task.TaskScheduler
import javax.inject.Inject
import javax.inject.Named

class ScheduleRestoreBackupTask @Inject constructor(
        @Named("restoreBackupScheduler") private val restoreBackupScheduler: TaskScheduler)
    : CompletableTask<CompletableTask.None> {
    override fun completable(params: CompletableTask.None): Completable {
        return Completable.fromCallable(restoreBackupScheduler::schedule)
    }

}
