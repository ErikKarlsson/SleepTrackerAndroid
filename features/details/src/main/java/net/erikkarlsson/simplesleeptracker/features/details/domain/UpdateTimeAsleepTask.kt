package net.erikkarlsson.simplesleeptracker.features.details.domain

import kotlinx.coroutines.flow.first
import net.erikkarlsson.simplesleeptracker.domain.SleepDataSource
import net.erikkarlsson.simplesleeptracker.domain.entity.Sleep
import net.erikkarlsson.simplesleeptracker.domain.task.CoroutineTask
import net.erikkarlsson.simplesleeptracker.domain.task.TaskScheduler
import org.threeten.bp.LocalTime
import javax.inject.Inject
import javax.inject.Named

class UpdateTimeAsleepTask @Inject constructor(
        private val sleepRepository: SleepDataSource,
        @Named("backupScheduler") private val backupScheduler: TaskScheduler
) : CoroutineTask<UpdateTimeAsleepTask.Params> {

    override suspend fun completable(params: Params) {
        val sleep = sleepRepository.getSleepFlow(params.sleepId).first()
        updateTimeAsleep(sleep, params.timeAsleep)
        backupScheduler.schedule()
    }

    private suspend fun updateTimeAsleep(sleep: Sleep, timeAsleep: LocalTime) {
        val toDate = checkNotNull(sleep.toDate)
        val fromDate = sleep.fromDate
        val startDate = fromDate.toLocalDate()
        val endTime = toDate.toLocalTime()
        val zoneOffset = fromDate.offset

        val updatedSleep = Sleep.from(id = sleep.id,
                                      startDate = startDate,
                                      startTime = timeAsleep,
                                      endTime = endTime,
                                      zoneOffset = zoneOffset)

        sleepRepository.update(updatedSleep)
    }

    data class Params(val sleepId: Int, val timeAsleep: LocalTime)
}
