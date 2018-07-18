package net.erikkarlsson.simplesleeptracker.feature.details.domain

import io.reactivex.Completable
import net.erikkarlsson.simplesleeptracker.domain.SleepDataSource
import net.erikkarlsson.simplesleeptracker.domain.entity.Sleep
import net.erikkarlsson.simplesleeptracker.domain.task.CompletableTask
import org.threeten.bp.LocalTime
import javax.inject.Inject

class UpdateTimeAwakeTask @Inject constructor(
        private val sleepRepository: SleepDataSource) : CompletableTask<UpdateTimeAwakeTask.Params> {

    override fun execute(params: Params): Completable =
            sleepRepository.getSleep(params.sleepId)
                    .take(1)
                    .map { updateTimeAwake(it, params.timeAwake) }
                    .ignoreElements()

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