package net.erikkarlsson.simplesleeptracker.domain

import com.nhaarman.mockito_kotlin.*
import io.reactivex.Completable
import io.reactivex.Single
import io.reactivex.subjects.Subject
import net.erikkarlsson.simplesleeptracker.base.MockDateTimeProvider
import net.erikkarlsson.simplesleeptracker.domain.entity.MinimumSleepEvent
import net.erikkarlsson.simplesleeptracker.domain.entity.Sleep
import net.erikkarlsson.simplesleeptracker.domain.entity.SleepEvent
import net.erikkarlsson.simplesleeptracker.domain.task.CompletableTask.None
import net.erikkarlsson.simplesleeptracker.domain.task.ScheduleBackupTask
import net.erikkarlsson.simplesleeptracker.domain.task.sleep.ToggleSleepTask
import net.erikkarlsson.simplesleeptracker.testutil.RxImmediateSchedulerRule
import org.junit.Rule
import org.junit.Test

class ToggleSleepTaskTest {

    @get:Rule
    var testSchedulerRule = RxImmediateSchedulerRule()

    val dateTimeProvider = MockDateTimeProvider()
    val sleepRepository: SleepDataSource = mock()
    val scheduleBackupTask: ScheduleBackupTask = mock()
    val appLifecycle: AppLifecycle = mock()
    val notifications: Notifications = mock()
    val sleepEvents: Subject<SleepEvent> = mock()
    val toggleSleepTask = ToggleSleepTask(sleepRepository, dateTimeProvider, scheduleBackupTask,
            appLifecycle, notifications, sleepEvents)

    @Test
    fun `toggle from empty inserts sleep`() {
        val now = dateTimeProvider.mockDateTime()
        val sleeping = Sleep(fromDate = now)
        given(sleepRepository.getCurrentSingle()).willReturn(Single.just(Sleep.empty()))
        toggleSleepTask.completable(None()).test().assertComplete()

        inOrder(sleepRepository) {
            verify(sleepRepository).getCurrentSingle()
            verify(sleepRepository).insert(sleeping)
            verifyNoMoreInteractions()
        }
    }

    @Test
    fun `toggle from sleeping updates to awake`() {
        val now = dateTimeProvider.mockDateTime()
        val anHourAgo = now.minusHours(1)
        val sleeping = Sleep(fromDate = anHourAgo)
        val awake = Sleep(fromDate = anHourAgo, toDate = now)
        given(sleepRepository.getCurrentSingle()).willReturn(Single.just(sleeping))
        given(scheduleBackupTask.completable(any())).willReturn(Completable.complete())

        toggleSleepTask.completable(None()).test().assertComplete()

        inOrder(sleepRepository) {
            verify(sleepRepository).getCurrentSingle()
            verify(sleepRepository).update(awake)
            verifyNoMoreInteractions()
        }

        verify(scheduleBackupTask).completable(any())
        verifyNoMoreInteractions(scheduleBackupTask)
    }

    @Test
    fun `toggle from awake inserts sleeping`() {
        val now = dateTimeProvider.mockDateTime()
        val sleeping = Sleep(fromDate = now)
        val awake = Sleep(fromDate = now, toDate = now.plusDays(1))
        given(sleepRepository.getCurrentSingle()).willReturn(Single.just(awake))
        toggleSleepTask.completable(None()).test().assertComplete()

        inOrder(sleepRepository) {
            verify(sleepRepository).getCurrentSingle()
            verify(sleepRepository).insert(sleeping)
            verifyNoMoreInteractions()
        }

        verifyNoMoreInteractions(scheduleBackupTask)
    }

    @Test
    fun `sleep duration less than an hour should not count as tracked night`() {
        val now = dateTimeProvider.mockDateTime()
        val sleeping = Sleep(fromDate = now)
        given(sleepRepository.getCurrentSingle()).willReturn(Single.just(sleeping))
        given(appLifecycle.isForegrounded()).willReturn(Single.just(true))
        dateTimeProvider.nowValue = now.plusMinutes(59)
        toggleSleepTask.completable(None()).test().assertComplete()

        inOrder(sleepRepository) {
            verify(sleepRepository).getCurrentSingle()
            verify(sleepRepository).delete(sleeping)
            verifyNoMoreInteractions()
        }

        verifyNoMoreInteractions(scheduleBackupTask)
    }

    @Test
    fun `sleep duration less than an hour should send sleep event when foregrounded`() {
        val now = dateTimeProvider.mockDateTime()
        val sleeping = Sleep(fromDate = now)
        given(sleepRepository.getCurrentSingle()).willReturn(Single.just(sleeping))
        given(appLifecycle.isForegrounded()).willReturn(Single.just(true))
        dateTimeProvider.nowValue = now.plusMinutes(59)
        toggleSleepTask.completable(None()).test().assertComplete()

        verify(sleepEvents).onNext(MinimumSleepEvent)
        verify(notifications, never()).sendMinimumSleepNotification()
        verifyNoMoreInteractions(sleepEvents)
    }

    @Test
    fun `sleep duration less than an hour should show notification when backgrounded`() {
        val now = dateTimeProvider.mockDateTime()
        val sleeping = Sleep(fromDate = now)
        given(sleepRepository.getCurrentSingle()).willReturn(Single.just(sleeping))
        given(appLifecycle.isForegrounded()).willReturn(Single.just(false))
        dateTimeProvider.nowValue = now.plusMinutes(59)
        toggleSleepTask.completable(None()).test().assertComplete()

        verify(sleepEvents, never()).onNext(MinimumSleepEvent)
        verify(notifications).sendMinimumSleepNotification()
        verifyNoMoreInteractions(notifications)
    }
}
