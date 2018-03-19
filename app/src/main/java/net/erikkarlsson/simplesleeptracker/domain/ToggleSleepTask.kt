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
            stopSleeping(currentSleep)
        } else {
            startSleeping()
        }
    }

    private fun startSleeping() {
        val sleep = Sleep(fromDate = dateTimeProvider.now())
        sleepRepository.insert(sleep)
    }

    private fun stopSleeping(currentSleep: Sleep) {
        val sleep = currentSleep.copy(toDate = dateTimeProvider.now())
        sleepRepository.update(sleep)
    }
}