package net.erikkarlsson.simplesleeptracker.domain.task

import io.reactivex.Completable
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.channels.BroadcastChannel
import kotlinx.coroutines.launch
import net.erikkarlsson.simplesleeptracker.domain.AppLifecycle
import net.erikkarlsson.simplesleeptracker.domain.DateTimeProvider
import net.erikkarlsson.simplesleeptracker.domain.Notifications
import net.erikkarlsson.simplesleeptracker.domain.SleepDataSource
import net.erikkarlsson.simplesleeptracker.domain.entity.MinimumSleepEvent
import net.erikkarlsson.simplesleeptracker.domain.entity.Sleep
import net.erikkarlsson.simplesleeptracker.domain.entity.SleepEvent
import net.erikkarlsson.simplesleeptracker.domain.task.CompletableTask.None
import javax.inject.Inject
import javax.inject.Named

private const val MINIMUM_SLEEP_DURATION_HOURS = 1 // Minimum hours to count as tracked sleep.

/**
 * Toggle between awake and asleep state.
 */
class ToggleSleepTask @Inject constructor(private val sleepRepository: SleepDataSource,
                                          private val dateTimeProvider: DateTimeProvider,
                                          private val scheduleBackupTask: ScheduleBackupTask,
                                          private val appLifecycle: AppLifecycle,
                                          private val notifications: Notifications,
                                          @Named("sleepEvents") private val sleepEvents: BroadcastChannel<SleepEvent>) : CompletableTask<None> {

    override fun completable(params: None): Completable =
            sleepRepository.getCurrentSingle()
                    .map(::toggleSleep)
                    .flatMapCompletable(::backupSleep)
                    .subscribeOn(Schedulers.io())

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
            minimumSleep(currentSleep)
            return false
        }
    }

    private fun minimumSleep(currentSleep: Sleep) {
        val isForegrounded = appLifecycle.isForegrounded().blockingGet()

        if (isForegrounded) {
            GlobalScope.launch (Dispatchers.Main) {
                sleepEvents.send(MinimumSleepEvent)
            }
        } else {
            notifications.sendMinimumSleepNotification()
        }

        sleepRepository.delete(currentSleep)
    }

    private fun backupSleep(shouldBackupSleep: Boolean): Completable =
            if (shouldBackupSleep) {
                scheduleBackupTask.completable(None())
            } else {
                Completable.complete()
            }

}
