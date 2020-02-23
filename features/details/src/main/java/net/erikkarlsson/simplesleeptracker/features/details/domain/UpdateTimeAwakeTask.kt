package net.erikkarlsson.simplesleeptracker.features.details.domain

import kotlinx.coroutines.flow.first
import net.erikkarlsson.simplesleeptracker.domain.SleepDataSource
import net.erikkarlsson.simplesleeptracker.domain.entity.Sleep
import net.erikkarlsson.simplesleeptracker.domain.task.CoroutineTask
import net.erikkarlsson.simplesleeptracker.domain.task.TaskScheduler
import org.threeten.bp.LocalTime
import javax.inject.Inject
import javax.inject.Named

class UpdateTimeAwakeTask @Inject constructor(
        private val sleepRepository: SleepDataSource,
        @Named("backupScheduler") private val backupScheduler: TaskScheduler) : CoroutineTask<UpdateTimeAwakeTask.Params> {

    override suspend fun completable(params: Params) {
        val sleep = sleepRepository.getSleepCoroutine(params.sleepId).first()
        updateTimeAwake(sleep, params.timeAwake)
        backupScheduler.schedule()
    }

    private fun updateTimeAwake(sleep: Sleep, timeAwake: LocalTime) {
        val fromDate = sleep.fromDate
        val startDate = fromDate.toLocalDate()
        val startTime = fromDate.toLocalTime()
        val zoneOffset = fromDate.offset

        val updatedSleep = Sleep.from(id = sleep.id,
                                      startDate = startDate,
                                      startTime = startTime,
                                      endTime = timeAwake,
                                      zoneOffset = zoneOffset)

        sleepRepository.update(updatedSleep)
    }

    data class Params(val sleepId: Int, val timeAwake: LocalTime)
}
