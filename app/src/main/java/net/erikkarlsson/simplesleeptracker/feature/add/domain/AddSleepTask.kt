package net.erikkarlsson.simplesleeptracker.feature.add.domain

import io.reactivex.Completable
import io.reactivex.Completable.fromCallable
import net.erikkarlsson.simplesleeptracker.domain.SleepDataSource
import net.erikkarlsson.simplesleeptracker.domain.entity.Sleep
import net.erikkarlsson.simplesleeptracker.domain.task.CompletableTask
import net.erikkarlsson.simplesleeptracker.feature.backup.domain.ScheduleBackupTask
import javax.inject.Inject

class AddSleepTask @Inject constructor(
        private val sleepRepository: SleepDataSource,
        private val scheduleBackupTask: ScheduleBackupTask)
    : CompletableTask<AddSleepTask.Params> {

    override fun execute(params: Params): Completable =
            fromCallable { sleepRepository.insert(params.sleep) }
                    .andThen(scheduleBackupTask.execute(CompletableTask.None()))

    data class Params(val sleep: Sleep)
}
