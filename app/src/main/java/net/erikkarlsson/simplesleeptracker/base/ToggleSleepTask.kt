package net.erikkarlsson.simplesleeptracker.base

import io.reactivex.Completable
import io.reactivex.Single
import net.erikkarlsson.simplesleeptracker.domain.Sleep
import net.erikkarlsson.simplesleeptracker.domain.SleepDataSource
import net.erikkarlsson.simplesleeptracker.util.SchedulerProvider
import org.threeten.bp.OffsetDateTime
import javax.inject.Inject

class ToggleSleepTask @Inject constructor(
        private val sleepRepository: SleepDataSource,
        private val schedulerProvider: SchedulerProvider) {

    internal fun execute(): Single<Sleep> =
            getCurrentSleep()
                .flatMap {
                    Completable.fromAction { toggleSleep(it) }
                        .andThen(getCurrentSleep())
                }
                .subscribeOn(schedulerProvider.io())

    private fun getCurrentSleep(): Single<Sleep> {
        return sleepRepository.getCurrent()
    }

    private fun toggleSleep(currentSleep: Sleep) {
        if (currentSleep.isSleeping) {
            stopSleeping(currentSleep)
        } else {
            startSleeping()
        }
    }

    private fun startSleeping() {
        val sleep = Sleep(fromDate = OffsetDateTime.now())
        sleepRepository.insert(sleep)
    }

    private fun stopSleeping(currentSleep: Sleep) {
        val sleep = currentSleep.copy(toDate = OffsetDateTime.now())
        sleepRepository.update(sleep)
    }
}