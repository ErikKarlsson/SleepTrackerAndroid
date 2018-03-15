package net.erikkarlsson.simplesleeptracker.domain

import io.reactivex.Completable
import org.threeten.bp.OffsetDateTime
import javax.inject.Inject

class ToggleSleepTask @Inject constructor(private val sleepRepository: SleepDataSource) {

    internal fun execute(): Completable =
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
        val sleep = Sleep(fromDate = OffsetDateTime.now())
        sleepRepository.insert(sleep)
    }

    private fun stopSleeping(currentSleep: Sleep) {
        val sleep = currentSleep.copy(toDate = OffsetDateTime.now())
        sleepRepository.update(sleep)
    }
}