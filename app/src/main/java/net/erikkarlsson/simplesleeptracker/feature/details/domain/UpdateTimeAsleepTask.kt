package net.erikkarlsson.simplesleeptracker.feature.details.domain

import io.reactivex.Completable
import net.erikkarlsson.simplesleeptracker.domain.SleepDataSource
import net.erikkarlsson.simplesleeptracker.domain.entity.Sleep
import net.erikkarlsson.simplesleeptracker.domain.task.CompletableTask
import org.threeten.bp.LocalTime
import javax.inject.Inject

class UpdateTimeAsleepTask @Inject constructor(
        private val sleepRepository: SleepDataSource) : CompletableTask<UpdateTimeAsleepTask.Params> {

    override fun execute(params: Params): Completable =
            sleepRepository.getSleep(params.sleepId)
                    .take(1)
                    .map { updateTimeAsleep(it, params.timeAsleep) }
                    .ignoreElements()

    private fun updateTimeAsleep(sleep: Sleep, timeAsleep: LocalTime) {
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
