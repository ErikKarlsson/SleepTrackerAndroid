package net.erikkarlsson.simplesleeptracker.domain.task.backup

import io.reactivex.Completable
import net.erikkarlsson.simplesleeptracker.domain.task.CompletableTask
import net.erikkarlsson.simplesleeptracker.domain.task.TaskScheduler
import javax.inject.Inject
import javax.inject.Named

class ScheduleBackupTask @Inject constructor(
        @Named("restoreBackupScheduler") private val restoreBackupScheduler: TaskScheduler
                                            )
    : CompletableTask<CompletableTask.None>
{
    override fun execute(params: CompletableTask.None): Completable {
        return Completable.fromCallable(restoreBackupScheduler::schedule)
    }

}