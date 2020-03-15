package net.erikkarlsson.simplesleeptracker.features.add.domain

import net.erikkarlsson.simplesleeptracker.domain.SleepDataSourceCoroutines
import net.erikkarlsson.simplesleeptracker.domain.entity.Sleep
import net.erikkarlsson.simplesleeptracker.domain.task.CoroutineTask
import net.erikkarlsson.simplesleeptracker.domain.task.TaskScheduler
import javax.inject.Inject
import javax.inject.Named

class AddSleepTask @Inject constructor(
        private val sleepRepository: SleepDataSourceCoroutines,
        @Named("backupScheduler") private val backupScheduler: TaskScheduler)
    : CoroutineTask<AddSleepTask.Params> {

    override suspend fun completable(params: Params) {
        sleepRepository.insert(params.sleep)
        backupScheduler.schedule()
    }

    data class Params(val sleep: Sleep)
}
