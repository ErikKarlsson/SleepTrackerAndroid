package net.erikkarlsson.simplesleeptracker.domain

import com.nhaarman.mockito_kotlin.*
import io.reactivex.Completable
import io.reactivex.Single
import net.erikkarlsson.simplesleeptracker.base.MockDateTimeProvider
import net.erikkarlsson.simplesleeptracker.domain.entity.Sleep
import net.erikkarlsson.simplesleeptracker.domain.task.CompletableTask.None
import net.erikkarlsson.simplesleeptracker.domain.task.sleep.ToggleSleepTask
import net.erikkarlsson.simplesleeptracker.feature.backup.domain.ScheduleBackupTask
import net.erikkarlsson.simplesleeptracker.testutil.RxImmediateSchedulerRule
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class ToggleSleepTaskTest {

    @get:Rule
    var testSchedulerRule = RxImmediateSchedulerRule()

    val dateTimeProvider = MockDateTimeProvider()
    val sleepRepository: SleepDataSource = mock()
    val scheduleBackupTask: ScheduleBackupTask = mock()
    val toggleSleepTask = ToggleSleepTask(sleepRepository, dateTimeProvider, scheduleBackupTask)

    @Test
    fun `toggle from empty inserts sleep`() {
        val now = dateTimeProvider.mockDateTime()
        val sleeping = Sleep(fromDate = now)
        given(sleepRepository.getCurrentSingle()).willReturn(Single.just(Sleep.empty()))
        toggleSleepTask.execute(None()).test().assertComplete()

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
        given(scheduleBackupTask.execute(any())).willReturn(Completable.complete())

        toggleSleepTask.execute(None()).test().assertComplete()

        inOrder(sleepRepository) {
            verify(sleepRepository).getCurrentSingle()
            verify(sleepRepository).update(awake)
            verifyNoMoreInteractions()
        }

        verify(scheduleBackupTask).execute(any())
        verifyNoMoreInteractions(scheduleBackupTask)
    }

    @Test
    fun `toggle from awake inserts sleeping`() {
        val now = dateTimeProvider.mockDateTime()
        val sleeping = Sleep(fromDate = now)
        val awake = Sleep(fromDate = now, toDate = now.plusDays(1))
        given(sleepRepository.getCurrentSingle()).willReturn(Single.just(awake))
        toggleSleepTask.execute(None()).test().assertComplete()

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
        dateTimeProvider.nowValue = now.plusMinutes(59)
        toggleSleepTask.execute(None()).test().assertComplete()

        inOrder(sleepRepository) {
            verify(sleepRepository).getCurrentSingle()
            verify(sleepRepository).delete(sleeping)
            verifyNoMoreInteractions()
        }

        verifyNoMoreInteractions(scheduleBackupTask)
    }
}