package net.erikkarlsson.simplesleeptracker.feature.home

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import com.google.common.collect.ImmutableList
import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.given
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.verify
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.subjects.PublishSubject
import io.reactivex.subjects.Subject
import net.erikkarlsson.simplesleeptracker.base.MockDateTimeProvider
import net.erikkarlsson.simplesleeptracker.domain.*
import net.erikkarlsson.simplesleeptracker.domain.entity.Sleep
import net.erikkarlsson.simplesleeptracker.domain.task.TaskScheduler
import net.erikkarlsson.simplesleeptracker.domain.task.sleep.ToggleSleepTask
import net.erikkarlsson.simplesleeptracker.feature.backup.domain.ScheduleBackupTask
import net.erikkarlsson.simplesleeptracker.feature.backup.domain.ScheduleRestoreBackupTask
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

    val widgetDataSource: WidgetDataSource = mock()

    val dateTimeProvider: DateTimeProvider = MockDateTimeProvider()

    val appLifecycle: AppLifecycle = mock()

    val notifications: Notifications = mock()

    val sleepEvents: Subject<SleepEvent> = PublishSubject.create()

    val scheduleBackupTask = ScheduleBackupTask(taskScheduler)
    val scheduleRestoreBackupTask = ScheduleRestoreBackupTask(taskScheduler)
    val logoutTask = LogoutTask(this.sleepDataSource, preferencesDataSource)
    val toggleSleepTask = ToggleSleepTask(this.sleepDataSource, dateTimeProvider,
            scheduleBackupTask, appLifecycle, notifications, sleepEvents)

    val homeSubscription = HomeSubscription(fileBackupDataSource, sleepDataSource)
    val eventSubscription = EventSubscription(sleepEvents)
    val homeEvents: HomeEvents = MutableLiveData()

    fun createViewModel(): HomeViewModel {
        val homeComponent = HomeComponent(toggleSleepTask, scheduleRestoreBackupTask,
                logoutTask, homeSubscription, eventSubscription, widgetDataSource, homeEvents)
        return HomeViewModel(homeComponent)
    }

    /**
     * See [ToggleSleepTaskTest] for extensive coverage of toggle cases.
     */
    @Test
    fun `clicking toggle sleep button toggles sleep`() {
        given(this.sleepDataSource.getSleep()).willReturn(Observable.just(ImmutableList.of()))
        given(this.sleepDataSource.getCurrent()).willReturn(Observable.just(Sleep.empty()))
        given(this.sleepDataSource.getCurrentSingle()).willReturn(Single.just(Sleep.empty()))
        given(this.sleepDataSource.getCount()).willReturn(Observable.just(0))
        given(fileBackupDataSource.getLastBackupTimestamp()).willReturn(Observable.just(0))

        val viewModel = createViewModel()

        viewModel.dispatch(ToggleSleepClicked)

        verify(this.sleepDataSource).insert(any())
    }

}
