package net.erikkarlsson.simplesleeptracker.features.details.domain

import kotlinx.coroutines.flow.first
import net.erikkarlsson.simplesleeptracker.domain.SleepDataSource
import net.erikkarlsson.simplesleeptracker.domain.task.CoroutineTask
import net.erikkarlsson.simplesleeptracker.domain.task.TaskScheduler
import javax.inject.Inject
import javax.inject.Named

class DeleteSleepTaskCoroutines @Inject constructor(
        private val sleepRepository: SleepDataSource,
        @Named("backupScheduler") private val backupScheduler: TaskScheduler)
    : CoroutineTask<DeleteSleepTaskCoroutines.Params> {

    override suspend fun completable(params: Params) {
        val sleep = sleepRepository.getSleepCoroutine(params.sleepId).first()
        sleepRepository.deleteCoroutines(sleep)
        backupScheduler.schedule()
    }

    data class Params(val sleepId: Int)
}
