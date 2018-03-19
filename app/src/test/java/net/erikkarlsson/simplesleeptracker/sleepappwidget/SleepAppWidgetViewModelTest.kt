package net.erikkarlsson.simplesleeptracker.sleepappwidget

import android.arch.lifecycle.Observer
import com.nhaarman.mockito_kotlin.*
import io.reactivex.Completable
import io.reactivex.Observable
import net.erikkarlsson.simplesleeptracker.domain.Sleep
import net.erikkarlsson.simplesleeptracker.domain.SleepDataSource
import net.erikkarlsson.simplesleeptracker.domain.ToggleSleepTask
import net.erikkarlsson.simplesleeptracker.domain.ToggleSleepTaskTest
import net.erikkarlsson.simplesleeptracker.util.InstantTaskExecutorExtension
import net.erikkarlsson.simplesleeptracker.util.RxImmediateSchedulerExtension
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.extension.ExtendWith
import org.threeten.bp.OffsetDateTime

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ExtendWith(InstantTaskExecutorExtension::class, RxImmediateSchedulerExtension::class)
class SleepAppWidgetViewModelTest {

    val sleepRepository: SleepDataSource = mock()
    val observer: Observer<WidgetState> = mock()
    val toggleSleepTask: ToggleSleepTask = mock()

    @BeforeEach
    fun setUp() {
        reset(sleepRepository, observer, toggleSleepTask)
    }

    @Nested
    inner class LoadCases {

        @Test
        fun `load empty shows awake in view`() {
            given(sleepRepository.getCurrent()).willReturn(Observable.just(Sleep.empty()))
            val viewModel = createViewModel()
            viewModel.state().observeForever(observer)
            verify(sleepRepository).getCurrent()
            verify(observer).onChanged(WidgetState(false, 0))
        }

        @Test
        fun `load sleeping shows sleeping in view`() {
            val sleeping = Sleep(fromDate = OffsetDateTime.now())
            given(sleepRepository.getCurrent()).willReturn(Observable.just(sleeping))
            val viewModel = createViewModel()
            viewModel.state().observeForever(observer)
            verify(sleepRepository).getCurrent()
            verify(observer).onChanged(WidgetState(true, 0))
        }

        @Test
        fun `load awake shows awake in view`() {
            val awake = Sleep(fromDate = OffsetDateTime.now(), toDate = OffsetDateTime.now().plusDays(1))
            given(sleepRepository.getCurrent()).willReturn(Observable.just(awake))
            val viewModel = createViewModel()
            viewModel.state().observeForever(observer)
            verify(sleepRepository).getCurrent()
            verify(observer).onChanged(WidgetState(false, 0))
        }

        @Test
        fun `load error shows awake in view`() {
            given(sleepRepository.getCurrent()).willReturn(Observable.error(Exception()))
            val viewModel = createViewModel()
            viewModel.state().observeForever(observer)
            verify(sleepRepository).getCurrent()
            verify(observer).onChanged(WidgetState(false, 0))
        }

    }

    @Nested
    inner class UpdateCases {

        @Test
        fun `on widget update renders view`() {
            given(sleepRepository.getCurrent()).willReturn(Observable.just(Sleep.empty()))
            val viewModel = createViewModel()
            viewModel.state().observeForever(observer)
            viewModel.dispatch(WidgetOnUpdate)
            inOrder(observer) {
                verify(observer).onChanged(WidgetState.empty())
                verify(observer).onChanged(WidgetState(false, 1))
            }
        }

    }

    @Nested
    inner class ToggleCases {

        /**
         * Verifies that clicking sleep toggle will execute task.
         * See [ToggleSleepTaskTest] for extensive coverage of toggle cases.
         */

        @Test
        fun `clicking toggle sleep button toggles sleep`() {
            given(toggleSleepTask.execute()).willReturn(Completable.complete())
            given(sleepRepository.getCurrent()).willReturn(Observable.just(Sleep.empty()))
            val viewModel = createViewModel()
            viewModel.dispatch(ToggleSleepClicked)
            verify(toggleSleepTask).execute()
        }
    }

    private fun createViewModel(): SleepAppWidgetViewModel {
        val sleepSubscription = SleepSubscription(sleepRepository)
        val widgetComponent = AppWidgetComponent(toggleSleepTask, sleepSubscription)
        return SleepAppWidgetViewModel(widgetComponent)
    }

}