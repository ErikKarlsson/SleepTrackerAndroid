package net.erikkarlsson.simplesleeptracker.sleepappwidget

import android.arch.lifecycle.Observer
import com.nhaarman.mockito_kotlin.given
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.reset
import com.nhaarman.mockito_kotlin.verify
import io.reactivex.Single
import net.erikkarlsson.simplesleeptracker.domain.Sleep
import net.erikkarlsson.simplesleeptracker.domain.SleepDataSource
import net.erikkarlsson.simplesleeptracker.sleepappwidget.processor.LoadCurrentSleep
import net.erikkarlsson.simplesleeptracker.sleepappwidget.processor.ToggleSleep
import net.erikkarlsson.simplesleeptracker.util.ImmediateSchedulerProvider
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

    private lateinit var viewModel: SleepAppWidgetViewModel
    val observer: Observer<WidgetState> = mock()

    private val schedulerProvider = ImmediateSchedulerProvider()
    private val sleeping = Sleep(fromDate = OffsetDateTime.now())
    private val awake = Sleep(fromDate = OffsetDateTime.now(),
            toDate = OffsetDateTime.now().plusDays(1))

    @BeforeEach
    fun setUp() {
        reset(sleepRepository)

        val loadCurrentSleep = LoadCurrentSleep(sleepRepository, schedulerProvider)
        val toggleSleep = ToggleSleep(sleepRepository, schedulerProvider)
        val widgetComponent = AppWidgetComponent(loadCurrentSleep, toggleSleep)

        viewModel = SleepAppWidgetViewModel(widgetComponent)
        viewModel.state().observeForever(observer)
    }

/*
    @Nested
    inner class LoadCases {

        @Test
        fun `load empty shows awake in view`() {
            given(sleepRepository.getCurrent()).willReturn(Single.just(Sleep.empty()))
            viewModel.dispatch(WidgetMsg.InitialMsg)
            verify(sleepRepository).getCurrent()
            verify(observer).onChanged(WidgetState(false))
        }

        @Test
        fun `load sleeping shows sleeping in view`() {
            given(sleepRepository.getCurrent()).willReturn(Single.just(sleeping))
            viewModel.dispatch(WidgetMsg.InitialMsg)
            verify(sleepRepository).getCurrent()
            verify(observer).onChanged(WidgetState(true))
        }

        @Test
        fun `load awake shows awake in view`() {
            given(sleepRepository.getCurrent()).willReturn(Single.just(awake))
            viewModel.dispatch(WidgetMsg.InitialMsg)
            verify(sleepRepository).getCurrent()
            verify(observer).onChanged(WidgetState(false))
        }

        @Test
        fun `load error shows awake in view`() {
            given(sleepRepository.getCurrent()).willReturn(Single.error(Exception()))
            viewModel.processIntents(Observable.just(WidgetIntent.InitialIntent))
            verify(sleepRepository).getCurrent()
            testObserver.assertValueAt(0) { !it.isSleeping }
        }
*/

    }

/*
    @Nested
    inner class ToggleCases {

        @Test
        fun `toggle from empty shows sleeping in view`() {
            given(sleepRepository.getCurrent()).willReturn(Single.just(Sleep.empty()), Single.just(sleeping))
            viewModel.processIntents(Observable.just(WidgetIntent.ToggleSleepIntent))
            inOrder(sleepRepository) {
                verify(sleepRepository).getCurrent()
                verify(sleepRepository).insert(any())
                verify(sleepRepository).getCurrent()
            }
            testObserver.assertValueAt(0) { it.isSleeping }
        }

        @Test
        fun `toggle from awake shows sleeping in view`() {
            given(sleepRepository.getCurrent()).willReturn(Single.just(awake), Single.just(sleeping))
            viewModel.processIntents(Observable.just(WidgetIntent.ToggleSleepIntent))
            inOrder(sleepRepository) {
                verify(sleepRepository).getCurrent()
                verify(sleepRepository).insert(any())
                verify(sleepRepository).getCurrent()
            }
            testObserver.assertValueAt(0) { it.isSleeping }
        }

        @Test
        fun `toggle from sleeping shows awake in view`() {
            given(sleepRepository.getCurrent()).willReturn(Single.just(sleeping), Single.just(awake))
            viewModel.processIntents(Observable.just(WidgetIntent.ToggleSleepIntent))
            inOrder(sleepRepository) {
                verify(sleepRepository).getCurrent()
                verify(sleepRepository).update(any())
                verify(sleepRepository).getCurrent()
            }
            testObserver.assertValueAt(0) { !it.isSleeping }
        }
    }
*/


}