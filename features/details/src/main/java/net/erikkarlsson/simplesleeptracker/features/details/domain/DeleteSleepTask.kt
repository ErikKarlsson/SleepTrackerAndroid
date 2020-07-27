package net.erikkarlsson.simplesleeptracker.features.details.domain

import kotlinx.coroutines.flow.first
import net.erikkarlsson.simplesleeptracker.domain.SleepDataSource
import net.erikkarlsson.simplesleeptracker.domain.task.CoroutineTask
import net.erikkarlsson.simplesleeptracker.domain.task.TaskScheduler
import javax.inject.Inject
import javax.inject.Named

class DeleteSleepTask @Inject constructor(
        private val sleepRepository: SleepDataSource,
        @Named("backupScheduler") private val backupScheduler: TaskScheduler
)
    : CoroutineTask<DeleteSleepTask.Params> {

    override suspend fun completable(params: Params) {
        val sleep = sleepRepository.getSleepFlow(params.sleepId).first()
        sleepRepository.delete(sleep)
        backupScheduler.schedule()
    }

    data class Params(val sleepId: Int)
}
