package net.erikkarlsson.simplesleeptracker.domain.task.sleep

import io.reactivex.Completable
import net.erikkarlsson.simplesleeptracker.domain.SleepDataSource
import net.erikkarlsson.simplesleeptracker.domain.entity.Sleep
import net.erikkarlsson.simplesleeptracker.domain.task.CompletableTask
import org.threeten.bp.LocalDate
import org.threeten.bp.temporal.ChronoUnit
import javax.inject.Inject

class UpdateStartDateTask @Inject constructor(
        private val sleepRepository: SleepDataSource) : CompletableTask<UpdateStartDateTask.Params> {

    override fun execute(params: Params): Completable =
            sleepRepository.getSleep(params.sleepId)
                    .take(1)
                    .map { updateStartDate(it, params.startDate)}
                    .ignoreElements()

    private fun updateStartDate(sleep: Sleep, startDate: LocalDate) {
        val days = ChronoUnit.DAYS.between(sleep.fromDate.toLocalDate(), startDate)
        val fromDate = sleep.fromDate.plusDays(days)
        val toDate = sleep.toDate?.plusDays(days)
        val updatedSleep = sleep.copy(fromDate = fromDate, toDate = toDate)
        sleepRepository.update(updatedSleep)
    }

    data class Params(val sleepId: Int, val startDate: LocalDate)
}