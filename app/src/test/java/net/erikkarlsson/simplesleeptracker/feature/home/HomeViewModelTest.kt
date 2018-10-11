package net.erikkarlsson.simplesleeptracker.feature.home

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.google.common.collect.ImmutableList
import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.given
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.verify
import io.reactivex.Observable
import io.reactivex.Single
import net.erikkarlsson.simplesleeptracker.base.MockDateTimeProvider
import net.erikkarlsson.simplesleeptracker.domain.DateTimeProvider
import net.erikkarlsson.simplesleeptracker.domain.FileBackupDataSource
import net.erikkarlsson.simplesleeptracker.domain.PreferencesDataSource
import net.erikkarlsson.simplesleeptracker.domain.SleepDataSource
import net.erikkarlsson.simplesleeptracker.domain.entity.Sleep
import net.erikkarlsson.simplesleeptracker.domain.task.TaskScheduler
import net.erikkarlsson.simplesleeptracker.domain.task.sleep.GetCurrentSleepTask
import net.erikkarlsson.simplesleeptracker.domain.task.sleep.ToggleSleepTask
import net.erikkarlsson.simplesleeptracker.feature.backup.domain.ScheduleBackupTask
import net.erikkarlsson.simplesleeptracker.feature.backup.domain.ScheduleRestoreBackupTask
import net.erikkarlsson.simplesleeptracker.feature.home.domain.GetProfileTask
import net.erikkarlsson.simplesleeptracker.feature.home.domain.LogoutTask
import net.erikkarlsson.simplesleeptracker.testutil.RxImmediateSchedulerRule
import org.junit.Rule
import org.junit.Test

class HomeViewModelTest {

    @get:Rule
    var testSchedulerRule = RxImmediateSchedulerRule()

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    val sleepDataSource: SleepDataSource = mock()

    val fileBackupDataSource: FileBackupDataSource = mock()

    val preferencesDataSource: PreferencesDataSource = mock()

    val taskScheduler: TaskScheduler = mock()

    val dateTimeProvider: DateTimeProvider = MockDateTimeProvider()

    val scheduleBackupTask = ScheduleBackupTask(taskScheduler)
    val scheduleRestoreBackupTask = ScheduleRestoreBackupTask(taskScheduler)
    val logoutTask = LogoutTask(sleepDataSource, preferencesDataSource)
    val toggleSleepTask = ToggleSleepTask(sleepDataSource, dateTimeProvider, scheduleBackupTask)
    val getProfileTask = GetProfileTask(fileBackupDataSource)
    val getCurrentSleepTask = GetCurrentSleepTask(sleepDataSource)
    val profileSubscription = ProfileSubscription(getProfileTask)
    val sleepSubscription = SleepSubscription(getCurrentSleepTask)

    fun createViewModel(): HomeViewModel {
        val homeComponent = HomeComponent(toggleSleepTask, scheduleRestoreBackupTask,
                logoutTask, profileSubscription, sleepSubscription)
        return HomeViewModel(homeComponent)
    }

    /**
     * See [ToggleSleepTaskTest] for extensive coverage of toggle cases.
     */
    @Test
    fun `clicking toggle sleep button toggles sleep`() {
        given(sleepDataSource.getSleep()).willReturn(Observable.just(ImmutableList.of()))
        given(sleepDataSource.getCurrent()).willReturn(Observable.just(Sleep.empty()))
        given(sleepDataSource.getCurrentSingle()).willReturn(Single.just(Sleep.empty()))
        given(fileBackupDataSource.getLastBackupTimestamp()).willReturn(Observable.just(0))

        val viewModel = createViewModel()

        viewModel.dispatch(ToggleSleepClicked)

        verify(sleepDataSource).insert(any())
    }

}