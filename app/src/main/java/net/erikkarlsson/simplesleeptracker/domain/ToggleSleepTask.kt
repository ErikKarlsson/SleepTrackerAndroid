package net.erikkarlsson.simplesleeptracker.domain

import io.reactivex.Completable
import javax.inject.Inject

/**
 * Toggle between awake and asleep state
 */
class ToggleSleepTask @Inject constructor(private val sleepRepository: SleepDataSource,
                                          private val dateTimeProvider: DateTimeProvider) {

    fun execute(): Completable =
            sleepRepository.getCurrentSingle()
                .map { toggleSleep(it) }
                .toCompletable()

    private fun toggleSleep(currentSleep: Sleep) {
        if (currentSleep.isSleeping) {
            awake(currentSleep)
        } else {
            asleep()
        }
    }

    private fun asleep() {
        val sleep = Sleep(fromDate = dateTimeProvider.now())
        sleepRepository.insert(sleep)
    }

    private fun awake(currentSleep: Sleep) {
        val sleep = currentSleep.copy(toDate = dateTimeProvider.now())
        sleepRepository.update(sleep)
    }
}