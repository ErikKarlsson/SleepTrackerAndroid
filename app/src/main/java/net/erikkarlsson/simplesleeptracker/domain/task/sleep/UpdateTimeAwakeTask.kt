package net.erikkarlsson.simplesleeptracker.domain.task.sleep

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
                    .map { updateTimeAsleep(it, params.timeAsleep)}
                    .ignoreElements()

    private fun updateTimeAsleep(sleep: Sleep, timeAsleep: LocalTime) {
        val fromDate = sleep.fromDate.with(timeAsleep)
        val updatedSleep = sleep.copy(toDate = fromDate)
        sleepRepository.update(updatedSleep)
    }

    data class Params(val sleepId: Int, val timeAsleep: LocalTime)
}