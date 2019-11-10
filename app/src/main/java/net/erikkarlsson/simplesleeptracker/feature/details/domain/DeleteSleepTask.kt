package net.erikkarlsson.simplesleeptracker.feature.details.domain

import io.reactivex.Completable
import net.erikkarlsson.simplesleeptracker.domain.SleepDataSource
import net.erikkarlsson.simplesleeptracker.domain.entity.Sleep
import net.erikkarlsson.simplesleeptracker.domain.task.CompletableTask
import net.erikkarlsson.simplesleeptracker.domain.task.ScheduleBackupTask
import javax.inject.Inject

class DeleteSleepTask @Inject constructor(
        private val sleepRepository: SleepDataSource,
        private val scheduleBackupTask: ScheduleBackupTask)
    : CompletableTask<DeleteSleepTask.Params> {

    override fun completable(params: Params): Completable =
            sleepRepository.getSleep(params.sleepId)
                    .take(1)
                    .map { deleteSleep(it)}
                    .ignoreElements()
                    .andThen(scheduleBackupTask.completable(CompletableTask.None()))

    private fun deleteSleep(sleep: Sleep) {
        sleepRepository.delete(sleep)
    }

    data class Params(val sleepId: Int)
}
