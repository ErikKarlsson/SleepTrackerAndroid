package net.erikkarlsson.simplesleeptracker.domain.task

import io.reactivex.Completable
import net.erikkarlsson.simplesleeptracker.domain.SleepDataSource
import net.erikkarlsson.simplesleeptracker.domain.entity.Sleep
import org.threeten.bp.LocalDate
import javax.inject.Inject

class UpdateStartDateTask @Inject constructor(
        private val sleepRepository: SleepDataSource) : CompletableTask<UpdateStartDateTask.Params> {

    override fun execute(params: Params): Completable =
            sleepRepository.getSleep(params.sleepId)
                    .take(1)
                    .map { updateStartDate(it, params.startDate)}
                    .ignoreElements()

    private fun updateStartDate(sleep: Sleep, startDate: LocalDate) {
        val fromDate = sleep.fromDate.with(startDate)
        val updatedSleep = sleep.copy(fromDate = fromDate)
        sleepRepository.update(updatedSleep)
    }

    data class Params(val sleepId: Int, val startDate: LocalDate)
}