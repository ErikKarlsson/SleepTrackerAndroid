package net.erikkarlsson.simplesleeptracker.sleepappwidget

import com.nhaarman.mockito_kotlin.*
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.observers.TestObserver
import net.erikkarlsson.simplesleeptracker.domain.Sleep
import net.erikkarlsson.simplesleeptracker.domain.SleepDataSource
import net.erikkarlsson.simplesleeptracker.sleepappwidget.processor.LoadCurrentSleep
import net.erikkarlsson.simplesleeptracker.sleepappwidget.processor.ToggleSleep
import net.erikkarlsson.simplesleeptracker.util.ImmediateSchedulerProvider
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.threeten.bp.OffsetDateTime

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class SleepAppWidgetViewModelTest {

    val sleepRepository: SleepDataSource = mock()

    private lateinit var viewModel: SleepAppWidgetViewModel
    private lateinit var testObserver: TestObserver<WidgetViewState>

    private val schedulerProvider = ImmediateSchedulerProvider()
    private val sleeping = Sleep(fromDate = OffsetDateTime.now())
    private val awake = Sleep(fromDate = OffsetDateTime.now(),
            toDate = OffsetDateTime.now().plusDays(1))

    @BeforeEach
    fun setUp() {
        reset(sleepRepository)

        val loadCurrentSleep = LoadCurrentSleep(sleepRepository, schedulerProvider)
        val toggleSleep = ToggleSleep(sleepRepository, schedulerProvider)
        val sleepActionProcessorHolder = SleepActionProcessorHolder(loadCurrentSleep, toggleSleep)

        viewModel = SleepAppWidgetViewModel(sleepActionProcessorHolder)
        testObserver = viewModel.states().test()
    }

    @Nested
    inner class LoadCases {

        @Test
        fun `load empty shows awake in view`() {
            given(sleepRepository.getCurrent()).willReturn(Single.just(Sleep.empty()))
            viewModel.processIntents(Observable.just(WidgetIntent.InitialIntent))
            verify(sleepRepository).getCurrent()
            testObserver.assertValueAt(0) { !it.isSleeping }
        }

        @Test
        fun `load sleeping shows sleeping in view`() {
            given(sleepRepository.getCurrent()).willReturn(Single.just(sleeping))
            viewModel.processIntents(Observable.just(WidgetIntent.InitialIntent))
            verify(sleepRepository).getCurrent()
            testObserver.assertValueAt(0) { it.isSleeping }
        }

        @Test
        fun `load awake shows awake in view`() {
            given(sleepRepository.getCurrent()).willReturn(Single.just(awake))
            viewModel.processIntents(Observable.just(WidgetIntent.InitialIntent))
            verify(sleepRepository).getCurrent()
            testObserver.assertValueAt(0) { !it.isSleeping }
        }

        @Test
        fun `load error shows awake in view`() {
            given(sleepRepository.getCurrent()).willReturn(Single.error(Exception()))
            viewModel.processIntents(Observable.just(WidgetIntent.InitialIntent))
            verify(sleepRepository).getCurrent()
            testObserver.assertValueAt(0) { !it.isSleeping }
        }

    }

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


}