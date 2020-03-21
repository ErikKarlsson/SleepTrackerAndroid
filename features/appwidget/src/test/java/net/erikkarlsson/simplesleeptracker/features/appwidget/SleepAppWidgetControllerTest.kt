package net.erikkarlsson.simplesleeptracker.features.appwidget

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.given
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import net.erikkarlsson.simplesleeptracker.domain.SleepDataSource
import net.erikkarlsson.simplesleeptracker.domain.entity.Sleep
import net.erikkarlsson.simplesleeptracker.domain.task.ToggleSleepTask
import net.erikkarlsson.simplesleeptracker.testutil.TestCoroutineRule
import org.junit.Rule
import org.junit.Test
import org.threeten.bp.OffsetDateTime

class SleepAppWidgetControllerTest {

    @get:Rule
    val testCoroutineRule = TestCoroutineRule()

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    val dispatchers = testCoroutineRule.testDispatcherProvider

    val sleepRepository: SleepDataSource = mock()
    val toggleSleepTask: ToggleSleepTask = mock()
    val sleepWidgetView: SleepWidgetView = mock()

    val sleepAppWidgetController = SleepAppWidgetController(toggleSleepTask, sleepWidgetView,
            sleepRepository, dispatchers)

    /**
     * LoadCases
     */

    @Test
    fun `load empty shows awake in view`() = testCoroutineRule.runBlockingTest {
        given(sleepRepository.getCurrentFlow()).willReturn(flowOf(Sleep.empty()))

        sleepAppWidgetController.initialize()

        verify(sleepRepository).getCurrentFlow()
        verify(sleepWidgetView).render(false)
    }

    @Test
    fun `load sleeping shows sleeping in view`() = testCoroutineRule.runBlockingTest {
        val sleeping = Sleep(fromDate = OffsetDateTime.now())
        given(sleepRepository.getCurrentFlow()).willReturn(flowOf(sleeping))
        sleepAppWidgetController.initialize()
        verify(sleepRepository).getCurrentFlow()
        verify(sleepWidgetView).render(true)
    }

    @Test
    fun `load awake shows awake in view`() = testCoroutineRule.runBlockingTest {
        val awake = Sleep(fromDate = OffsetDateTime.now(), toDate = OffsetDateTime.now().plusDays(1))
        given(sleepRepository.getCurrentFlow()).willReturn(flowOf(awake))
        sleepAppWidgetController.initialize()
        verify(sleepRepository).getCurrentFlow()
        verify(sleepWidgetView).render(false)
    }

    /**
     * ToggleCases
     */

    /**
     * Verifies that clicking sleep toggle will completable task.
     * See [ToggleSleepTaskTest] for extensive coverage of toggle cases.
     */

    @Test
    fun `clicking toggle sleep button toggles sleep`() = testCoroutineRule.runBlockingTest {
        given(sleepRepository.getCurrentFlow()).willReturn(flowOf(Sleep.empty()))
        sleepAppWidgetController.onToggleSleepClick()

        runBlocking {
            verify(toggleSleepTask).completable(any())
        }
    }

}
