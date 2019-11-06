package net.erikkarlsson.simplesleeptracker.feature.appwidget

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.given
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.verify
import io.reactivex.Completable
import io.reactivex.Observable
import net.erikkarlsson.simplesleeptracker.domain.SleepDataSource
import net.erikkarlsson.simplesleeptracker.domain.entity.Sleep
import net.erikkarlsson.simplesleeptracker.domain.task.sleep.ToggleSleepTask
import net.erikkarlsson.simplesleeptracker.testutil.RxImmediateSchedulerRule
import org.junit.Rule
import org.junit.Test
import org.threeten.bp.OffsetDateTime

class SleepAppWidgetControllerTest {

    @get:Rule
    var testSchedulerRule = RxImmediateSchedulerRule()

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    val sleepRepository: SleepDataSource = mock()
    val toggleSleepTask: ToggleSleepTask = mock()
    val sleepWidgetView: SleepWidgetView = mock()

    val sleepAppWidgetController = SleepAppWidgetController(toggleSleepTask, sleepWidgetView, sleepRepository)

    /**
     * LoadCases
     */

    @Test
    fun `load empty shows awake in view`() {
        given(sleepRepository.getCurrent()).willReturn(Observable.just(Sleep.empty()))

        sleepAppWidgetController.updateWidget()

        verify(sleepRepository).getCurrent()
        verify(sleepWidgetView).render(false)
    }

    @Test
    fun `load sleeping shows sleeping in view`() {
        val sleeping = Sleep(fromDate = OffsetDateTime.now())
        given(sleepRepository.getCurrent()).willReturn(Observable.just(sleeping))
        sleepAppWidgetController.updateWidget()
        verify(sleepRepository).getCurrent()
        verify(sleepWidgetView).render(true)
    }

    @Test
    fun `load awake shows awake in view`() {
        val awake = Sleep(fromDate = OffsetDateTime.now(), toDate = OffsetDateTime.now().plusDays(1))
        given(sleepRepository.getCurrent()).willReturn(Observable.just(awake))
        sleepAppWidgetController.updateWidget()
        verify(sleepRepository).getCurrent()
        verify(sleepWidgetView).render(false)
    }

    @Test
    fun `load error shows awake in view`() {
        given(sleepRepository.getCurrent()).willReturn(Observable.error(Exception()))
        sleepAppWidgetController.updateWidget()
        verify(sleepRepository).getCurrent()
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
    fun `clicking toggle sleep button toggles sleep`() {
        given(toggleSleepTask.completable(any())).willReturn(Completable.complete())
        given(sleepRepository.getCurrent()).willReturn(Observable.just(Sleep.empty()))
        sleepAppWidgetController.onToggleSleepClick()
        verify(toggleSleepTask).completable(any())
    }

}
