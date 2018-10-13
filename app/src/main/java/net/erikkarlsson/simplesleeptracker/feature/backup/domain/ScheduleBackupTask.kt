package net.erikkarlsson.simplesleeptracker.feature.backup.domain

import io.reactivex.Completable
import net.erikkarlsson.simplesleeptracker.domain.task.CompletableTask
import net.erikkarlsson.simplesleeptracker.domain.task.TaskScheduler
import javax.inject.Inject
import javax.inject.Named

class ScheduleBackupTask @Inject constructor(
        @Named("backupScheduler") private val backupScheduler: TaskScheduler)
    : CompletableTask<CompletableTask.None> {
    override fun execute(params: CompletableTask.None): Completable {
        return Completable.fromCallable {
            backupScheduler.schedule()
        }
    }

}
