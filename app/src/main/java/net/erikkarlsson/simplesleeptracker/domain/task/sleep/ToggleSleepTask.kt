package net.erikkarlsson.simplesleeptracker.domain.task.sleep

import io.reactivex.Completable
import net.erikkarlsson.simplesleeptracker.domain.DateTimeProvider
import net.erikkarlsson.simplesleeptracker.domain.SleepDataSource
import net.erikkarlsson.simplesleeptracker.domain.entity.Sleep
import net.erikkarlsson.simplesleeptracker.domain.task.CompletableTask
import net.erikkarlsson.simplesleeptracker.domain.task.CompletableTask.None
import net.erikkarlsson.simplesleeptracker.feature.backup.domain.ScheduleBackupTask
import javax.inject.Inject

private const val MINIMUM_SLEEP_DURATION_HOURS = 1 // Minimum hours to count as tracked sleep.

/**
 * Toggle between awake and asleep state.
 */
class ToggleSleepTask @Inject constructor(private val sleepRepository: SleepDataSource,
                                          private val dateTimeProvider: DateTimeProvider,
                                          private val scheduleBackupTask: ScheduleBackupTask) : CompletableTask<None> {

    override fun execute(params: None): Completable =
            sleepRepository.getCurrentSingle()
                    .map(::toggleSleep)
                    .flatMapCompletable(::backupSleep)

    private fun toggleSleep(currentSleep: Sleep): Boolean =
            if (currentSleep.isSleeping) {
                awake(currentSleep)
            } else {
                asleep()
            }

    private fun asleep(): Boolean {
        val sleep = Sleep(fromDate = dateTimeProvider.now())
        sleepRepository.insert(sleep)
        return false
    }

    private fun awake(currentSleep: Sleep): Boolean {
        val sleep = currentSleep.copy(toDate = dateTimeProvider.now())

        if (sleep.hours >= MINIMUM_SLEEP_DURATION_HOURS) {
            sleepRepository.update(sleep)
            return true
        } else {
            sleepRepository.delete(currentSleep)
            return false
        }
    }

    private fun backupSleep(shouldBackupSleep: Boolean): Completable =
            if (shouldBackupSleep) {
                scheduleBackupTask.execute(None())
            } else {
                Completable.complete()
            }
}