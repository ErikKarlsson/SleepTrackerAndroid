package net.erikkarlsson.simplesleeptracker.domain

import com.nhaarman.mockitokotlin2.*
import kotlinx.coroutines.channels.BroadcastChannel
import net.erikkarlsson.simplesleeptracker.domain.entity.MinimumSleepEvent
import net.erikkarlsson.simplesleeptracker.domain.entity.Sleep
import net.erikkarlsson.simplesleeptracker.domain.entity.SleepEvent
import net.erikkarlsson.simplesleeptracker.domain.task.CoroutineTask
import net.erikkarlsson.simplesleeptracker.domain.task.TaskScheduler
import net.erikkarlsson.simplesleeptracker.domain.task.ToggleSleepTask
import net.erikkarlsson.simplesleeptracker.testutil.MockDateTimeProvider
import net.erikkarlsson.simplesleeptracker.testutil.RxImmediateSchedulerRule
import net.erikkarlsson.simplesleeptracker.testutil.TestCoroutineRule
import org.junit.Rule
import org.junit.Test

class ToggleSleepTaskTest {

    @get:Rule
    val testCoroutineRule = TestCoroutineRule()

    @get:Rule
    var testSchedulerRule = RxImmediateSchedulerRule()

    val dateTimeProvider = MockDateTimeProvider()
    val sleepRepository: SleepDataSource = mock()
    val backupScheduler: TaskScheduler = mock()
    val appLifecycle: AppLifecycle = mock()
    val notifications: Notifications = mock()
    val sleepEvents: BroadcastChannel<SleepEvent> = mock()
    val toggleSleepTask = ToggleSleepTask(sleepRepository, dateTimeProvider,
            appLifecycle, notifications, backupScheduler, sleepEvents)

    @Test
    fun `toggle from empty inserts sleep`() = testCoroutineRule.runBlockingTest {
            val now = dateTimeProvider.mockDateTime()
            val sleeping = Sleep(fromDate = now)
            given(sleepRepository.getCurrent()).willReturn(Sleep.empty())

            toggleSleepTask.completable(CoroutineTask.None())

            inOrder(sleepRepository) {
                verify(sleepRepository).getCurrent()
                verify(sleepRepository).insert(sleeping)
                verifyNoMoreInteractions()
            }
    }

    @Test
    fun `toggle from sleeping updates to awake`() = testCoroutineRule.runBlockingTest {
        val now = dateTimeProvider.mockDateTime()
        val anHourAgo = now.minusHours(1)
        val sleeping = Sleep(fromDate = anHourAgo)
        val awake = Sleep(fromDate = anHourAgo, toDate = now)
        given(sleepRepository.getCurrent()).willReturn(sleeping)

        toggleSleepTask.completable(CoroutineTask.None())

        inOrder(sleepRepository) {
            verify(sleepRepository).getCurrent()
            verify(sleepRepository).update(awake)
            verifyNoMoreInteractions()
        }

        verify(backupScheduler).schedule()
        verifyNoMoreInteractions(backupScheduler)
    }

    @Test
    fun `toggle from awake inserts sleeping`() = testCoroutineRule.runBlockingTest {
        val now = dateTimeProvider.mockDateTime()
        val sleeping = Sleep(fromDate = now)
        val awake = Sleep(fromDate = now, toDate = now.plusDays(1))
        given(sleepRepository.getCurrent()).willReturn(awake)
        toggleSleepTask.completable(CoroutineTask.None())

        inOrder(sleepRepository) {
            verify(sleepRepository).getCurrent()
            verify(sleepRepository).insert(sleeping)
            verifyNoMoreInteractions()
        }

        verifyNoMoreInteractions(backupScheduler)
    }

    @Test
    fun `sleep duration less than an hour should not count as tracked night`() = testCoroutineRule.runBlockingTest {
        val now = dateTimeProvider.mockDateTime()
        val sleeping = Sleep(fromDate = now)
        given(sleepRepository.getCurrent()).willReturn(sleeping)
        given(appLifecycle.isForegrounded()).willReturn(true)
        dateTimeProvider.nowValue = now.plusMinutes(59)
        toggleSleepTask.completable(CoroutineTask.None())

        inOrder(sleepRepository) {
            verify(sleepRepository).getCurrent()
            verify(sleepRepository).delete(sleeping)
            verifyNoMoreInteractions()
        }

        verifyNoMoreInteractions(backupScheduler)
    }

    @Test
    fun `sleep duration less than an hour should send sleep event when foregrounded`() = testCoroutineRule.runBlockingTest {
        val now = dateTimeProvider.mockDateTime()
        val sleeping = Sleep(fromDate = now)
        given(sleepRepository.getCurrent()).willReturn(sleeping)
        given(appLifecycle.isForegrounded()).willReturn(true)
        dateTimeProvider.nowValue = now.plusMinutes(59)
        toggleSleepTask.completable(CoroutineTask.None())

        verify(sleepEvents).send(MinimumSleepEvent)
        verify(notifications, never()).sendMinimumSleepNotification()
        verifyNoMoreInteractions(sleepEvents)
    }

    @Test
    fun `sleep duration less than an hour should show notification when backgrounded`() = testCoroutineRule.runBlockingTest {
        val now = dateTimeProvider.mockDateTime()
        val sleeping = Sleep(fromDate = now)
        given(sleepRepository.getCurrent()).willReturn(sleeping)
        given(appLifecycle.isForegrounded()).willReturn(false)
        dateTimeProvider.nowValue = now.plusMinutes(59)
        toggleSleepTask.completable(CoroutineTask.None())

        verify(sleepEvents, never()).send(MinimumSleepEvent)
        verify(notifications).sendMinimumSleepNotification()
        verifyNoMoreInteractions(notifications)
    }
}
