package net.erikkarlsson.simplesleeptracker.base

import io.reactivex.Single
import net.erikkarlsson.simplesleeptracker.domain.Sleep
import net.erikkarlsson.simplesleeptracker.domain.SleepDataSource
import net.erikkarlsson.simplesleeptracker.util.SchedulerProvider
import javax.inject.Inject

class LoadCurrentSleepTask @Inject constructor(
        private val sleepRepository: SleepDataSource,
        private val schedulerProvider: SchedulerProvider) {

    internal fun execute(): Single<Sleep> =
            sleepRepository.getCurrent().subscribeOn(schedulerProvider.io())
}