package net.erikkarlsson.simplesleeptracker.feature.add.domain

import io.reactivex.Completable
import net.erikkarlsson.simplesleeptracker.domain.SleepDataSource
import net.erikkarlsson.simplesleeptracker.domain.entity.Sleep
import net.erikkarlsson.simplesleeptracker.domain.task.CompletableTask
import javax.inject.Inject

class AddSleepTask @Inject constructor(
        private val sleepRepository: SleepDataSource)
    : CompletableTask<AddSleepTask.Params> {

    override fun execute(params: Params): Completable =
        Completable.fromCallable { sleepRepository.insert(params.sleep) }

    data class Params(val sleep: Sleep)
}