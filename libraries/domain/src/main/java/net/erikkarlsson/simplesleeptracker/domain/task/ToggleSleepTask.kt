package net.erikkarlsson.simplesleeptracker.domain.task

import kotlinx.coroutines.channels.BroadcastChannel
import net.erikkarlsson.simplesleeptracker.domain.AppLifecycle
import net.erikkarlsson.simplesleeptracker.domain.DateTimeProvider
import net.erikkarlsson.simplesleeptracker.domain.Notifications
import net.erikkarlsson.simplesleeptracker.domain.SleepDataSourceCoroutines
import net.erikkarlsson.simplesleeptracker.domain.entity.MinimumSleepEvent
import net.erikkarlsson.simplesleeptracker.domain.entity.Sleep
import net.erikkarlsson.simplesleeptracker.domain.entity.SleepEvent
import net.erikkarlsson.simplesleeptracker.domain.task.CoroutineTask.None
import javax.inject.Inject
import javax.inject.Named

private const val MINIMUM_SLEEP_DURATION_HOURS = 1 // Minimum hours to count as tracked sleep.

/**
 * Toggle between awake and asleep state.
 */
class ToggleSleepTask @Inject constructor(private val sleepRepository: SleepDataSourceCoroutines,
                                          private val dateTimeProvider: DateTimeProvider,
                                          private val appLifecycle: AppLifecycle,
                                          private val notifications: Notifications,
                                          @Named("backupScheduler") private val backupScheduler: TaskScheduler,
                                          @Named("sleepEvents") private val sleepEvents: BroadcastChannel<SleepEvent>) : CoroutineTask<None> {

    override suspend fun completable(params: None) {
        val sleep = sleepRepository.getCurrent()
        val shouldBackupSleep = toggleSleep(sleep)

        if (shouldBackupSleep) {
            backupScheduler.schedule()
        }
    }

    private suspend fun toggleSleep(currentSleep: Sleep): Boolean =
            when (currentSleep.isSleeping) {
                true -> awake(currentSleep)
                false -> asleep()
            }

    private suspend fun asleep(): Boolean {
        val sleep = Sleep(fromDate = dateTimeProvider.now())
        sleepRepository.insert(sleep)
        return false
    }

    private suspend fun awake(currentSleep: Sleep): Boolean {
        val sleep = currentSleep.copy(toDate = dateTimeProvider.now())

        if (sleep.hours >= MINIMUM_SLEEP_DURATION_HOURS) {
            sleepRepository.update(sleep)
            return true
        } else {
            minimumSleep(currentSleep)
            return false
        }
    }

    private suspend fun minimumSleep(currentSleep: Sleep) {
        val isForegrounded = appLifecycle.isForegrounded().blockingGet()

        when (isForegrounded) {
            true -> sleepEvents.send(MinimumSleepEvent)
            false -> notifications.sendMinimumSleepNotification()
        }

        sleepRepository.delete(currentSleep)
    }
}
