package net.erikkarlsson.simplesleeptracker.features.home

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.given
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import kotlinx.coroutines.channels.BroadcastChannel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flowOf
import net.erikkarlsson.simplesleeptracker.domain.*
import net.erikkarlsson.simplesleeptracker.domain.entity.Sleep
import net.erikkarlsson.simplesleeptracker.domain.entity.SleepEvent
import net.erikkarlsson.simplesleeptracker.domain.task.TaskScheduler
import net.erikkarlsson.simplesleeptracker.domain.task.ToggleSleepTask
import net.erikkarlsson.simplesleeptracker.features.home.domain.GetHomeTask
import net.erikkarlsson.simplesleeptracker.features.home.domain.LogoutTask
import net.erikkarlsson.simplesleeptracker.testutil.MockDateTimeProvider
import net.erikkarlsson.simplesleeptracker.testutil.TestCoroutineRule
import org.junit.Ignore
import org.junit.Rule
import org.junit.Test

class HomeViewModelTest {

    @get:Rule
    val testCoroutineRule = TestCoroutineRule()

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

    val sleepEvents: BroadcastChannel<SleepEvent> = BroadcastChannel(1)

    val getHomeTask = GetHomeTask(fileBackupDataSource, sleepDataSource)
    val logoutTask = LogoutTask(this.sleepDataSource, preferencesDataSource)
    val toggleSleepTask = ToggleSleepTask(this.sleepDataSource, dateTimeProvider,
            appLifecycle, notifications, taskScheduler, sleepEvents)

    val homeEvents: HomeEvents = MutableLiveData()

    fun createViewModel(): HomeViewModel {
        return HomeViewModel(getHomeTask, toggleSleepTask, logoutTask, widgetDataSource,
                sleepEvents, taskScheduler, homeEvents)
    }

    /**
     * See [ToggleSleepTaskTest] for extensive coverage of toggle cases.
     */

    @Test
    fun `clicking toggle sleep button toggles sleep`() = testCoroutineRule.runBlockingTest {
        given(sleepDataSource.getCurrentFlow()).willReturn(flowOf(Sleep.empty()))
        given(sleepDataSource.getCurrent()).willReturn(Sleep.empty())
        given(sleepDataSource.getCountFlow()).willReturn(flowOf(0))
        given(fileBackupDataSource.getLastBackupTimestamp()).willReturn(flowOf(0))

        val viewModel = createViewModel()

        viewModel.onToggleSleepClick()

        verify(sleepDataSource).insert(any())
    }

}
