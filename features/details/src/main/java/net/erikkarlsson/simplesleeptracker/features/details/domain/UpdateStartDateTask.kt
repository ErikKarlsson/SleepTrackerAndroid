package net.erikkarlsson.simplesleeptracker.features.details.domain

import kotlinx.coroutines.flow.first
import net.erikkarlsson.simplesleeptracker.domain.SleepDataSource
import net.erikkarlsson.simplesleeptracker.domain.entity.shiftStartDate
import net.erikkarlsson.simplesleeptracker.domain.task.CoroutineTask
import net.erikkarlsson.simplesleeptracker.domain.task.TaskScheduler
import org.threeten.bp.LocalDate
import javax.inject.Inject
import javax.inject.Named

class UpdateStartDateTask @Inject constructor(
        private val sleepRepository: SleepDataSource,
        @Named("backupScheduler") private val backupScheduler: TaskScheduler)
    : CoroutineTask<UpdateStartDateTask.Params> {

    override suspend fun completable(params: Params) {
        val startDate = params.startDate
        val sleep = sleepRepository.getSleepFlow(params.sleepId).first()
        val updatedSleep = sleep.shiftStartDate(startDate)
        sleepRepository.update(updatedSleep)
        backupScheduler.schedule()
    }

    data class Params(val sleepId: Int, val startDate: LocalDate)
}
