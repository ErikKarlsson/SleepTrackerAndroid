package net.erikkarlsson.simplesleeptracker.feature.appwidget
/*
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.nhaarman.mockito_kotlin.*
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
    val observer: Observer<WidgetState> = mock()
    val toggleSleepTask: ToggleSleepTask = mock()

    /**
     * LoadCases
     */

    @Test
    fun `load empty shows awake in view`() {
        given(sleepRepository.getCurrent()).willReturn(Observable.just(Sleep.empty()))
        val viewModel = createViewModel()
        viewModel.state().observeForever(observer)
        verify(sleepRepository).getCurrent()
        verify(observer).onChanged(WidgetState(false, false, 0))
    }

    @Test
    fun `load sleeping shows sleeping in view`() {
        val sleeping = Sleep(fromDate = OffsetDateTime.now())
        given(sleepRepository.getCurrent()).willReturn(Observable.just(sleeping))
        val viewModel = createViewModel()
        viewModel.state().observeForever(observer)
        verify(sleepRepository).getCurrent()
        verify(observer).onChanged(WidgetState(false,true, 0))
    }

    @Test
    fun `load awake shows awake in view`() {
        val awake = Sleep(fromDate = OffsetDateTime.now(), toDate = OffsetDateTime.now().plusDays(1))
        given(sleepRepository.getCurrent()).willReturn(Observable.just(awake))
        val viewModel = createViewModel()
        viewModel.state().observeForever(observer)
        verify(sleepRepository).getCurrent()
        verify(observer).onChanged(WidgetState(false, false, 0))
    }

    @Test
    fun `load error shows awake in view`() {
        given(sleepRepository.getCurrent()).willReturn(Observable.error(Exception()))
        val viewModel = createViewModel()
        viewModel.state().observeForever(observer)
        verify(sleepRepository).getCurrent()
        verify(observer).onChanged(WidgetState(false, false, 0))
    }

    /**
     * UpdateCases
     */

    @Test
    fun `on widget update renders view`() {
        given(sleepRepository.getCurrent()).willReturn(Observable.just(Sleep.empty()))
        val viewModel = createViewModel()
        viewModel.state().observeForever(observer)
        viewModel.dispatch(WidgetOnUpdate)
        inOrder(observer) {
            verify(observer).onChanged(WidgetState(false,false, 1))
            verify(observer, never()).onChanged(any())
        }
    }

    /**
     * ToggleCases
     */

    /**
     * Verifies that clicking sleep toggle will execute task.
     * See [ToggleSleepTaskTest] for extensive coverage of toggle cases.
     */

    @Test
    fun `clicking toggle sleep button toggles sleep`() {
        given(toggleSleepTask.execute(any())).willReturn(Completable.complete())
        given(sleepRepository.getCurrent()).willReturn(Observable.just(Sleep.empty()))
        val viewModel = createViewModel()
        viewModel.dispatch(ToggleSleepClicked)
        verify(toggleSleepTask).execute(any())
    }

    private fun createViewModel(): SleepAppWidgetController {
        val sleepSubscription = SleepSubscription(sleepRepository)
        val widgetComponent = AppWidgetComponent(toggleSleepTask, sleepSubscription)
        return SleepAppWidgetController(widgetComponent)
    }

}
*/
