package net.erikkarlsson.simplesleeptracker.features.details.domain

import io.reactivex.Completable
import net.erikkarlsson.simplesleeptracker.domain.SleepDataSource
import net.erikkarlsson.simplesleeptracker.domain.entity.Sleep
import net.erikkarlsson.simplesleeptracker.domain.entity.shiftStartDate
import net.erikkarlsson.simplesleeptracker.domain.task.CompletableTask
import net.erikkarlsson.simplesleeptracker.domain.task.ScheduleBackupTask
import org.threeten.bp.LocalDate
import javax.inject.Inject

class UpdateStartDateTask @Inject constructor(
        private val sleepRepository: SleepDataSource,
        private val scheduleBackupTask: ScheduleBackupTask) : CompletableTask<UpdateStartDateTask.Params> {

    override fun completable(params: Params): Completable =
            sleepRepository.getSleep(params.sleepId)
                    .take(1)
                    .map { updateStartDate(it, params.startDate)}
                    .ignoreElements()
                    .andThen(scheduleBackupTask.completable(CompletableTask.None()))

    private fun updateStartDate(sleep: Sleep, startDate: LocalDate) {
        val updatedSleep = sleep.shiftStartDate(startDate)
        sleepRepository.update(updatedSleep)
    }

    data class Params(val sleepId: Int, val startDate: LocalDate)
}
