package net.erikkarlsson.simplesleeptracker.sleepappwidget

import com.nhaarman.mockito_kotlin.*
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.observers.TestObserver
import net.erikkarlsson.simplesleeptracker.domain.Sleep
import net.erikkarlsson.simplesleeptracker.domain.SleepDataSource
import net.erikkarlsson.simplesleeptracker.util.ImmediateSchedulerProvider
import net.erikkarlsson.simplesleeptracker.util.SchedulerProvider
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.threeten.bp.OffsetDateTime

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class SleepAppWidgetViewModelTest {

    val sleepDataSource: SleepDataSource = mock()

    private lateinit var viewModel: SleepAppWidgetViewModel
    private lateinit var testObserver: TestObserver<WidgetViewState>

    private val schedulerProvider: SchedulerProvider = ImmediateSchedulerProvider()
    private val sleeping = Sleep(fromDate = OffsetDateTime.now())
    private val awake = Sleep(fromDate = OffsetDateTime.now(),
            toDate = OffsetDateTime.now().plusDays(1))

    @BeforeEach
    fun init() {
        reset(sleepDataSource)
        viewModel = SleepAppWidgetViewModel(SleepActionProcessorHolder(sleepDataSource,
                schedulerProvider))
        testObserver = viewModel.states().test()
    }

    @Nested
    inner class LoadCases {

        @Test
        fun `load empty shows awake in view`() {
            given(sleepDataSource.getCurrent()).willReturn(Single.just(Sleep.empty()))
            viewModel.processIntents(Observable.just(WidgetIntent.InitialIntent))
            verify(sleepDataSource).getCurrent()
            testObserver.assertValueAt(0) { !it.isSleeping }
        }

        @Test
        fun `load sleeping shows sleeping in view`() {
            given(sleepDataSource.getCurrent()).willReturn(Single.just(sleeping))
            viewModel.processIntents(Observable.just(WidgetIntent.InitialIntent))
            verify(sleepDataSource).getCurrent()
            testObserver.assertValueAt(0) { it.isSleeping }
        }

        @Test
        fun `load awake shows awake in view`() {
            given(sleepDataSource.getCurrent()).willReturn(Single.just(awake))
            viewModel.processIntents(Observable.just(WidgetIntent.InitialIntent))
            verify(sleepDataSource).getCurrent()
            testObserver.assertValueAt(0) { !it.isSleeping }
        }

        @Test
        fun `load error shows awake in view`() {
            given(sleepDataSource.getCurrent()).willReturn(Single.error(Exception()))
            viewModel.processIntents(Observable.just(WidgetIntent.InitialIntent))
            verify(sleepDataSource).getCurrent()
            testObserver.assertValueAt(0) { !it.isSleeping }
        }

    }

    @Nested
    inner class ToggleCases {

        @Test
        fun `toggle from empty shows sleeping in view`() {
            given(sleepDataSource.getCurrent()).willReturn(Single.just(Sleep.empty()), Single.just(sleeping))
            viewModel.processIntents(Observable.just(WidgetIntent.ToggleSleepIntent))
            inOrder(sleepDataSource) {
                verify(sleepDataSource).getCurrent()
                verify(sleepDataSource).insert(any())
                verify(sleepDataSource).getCurrent()
            }
            testObserver.assertValueAt(0) { it.isSleeping }
        }

        @Test
        fun `toggle from awake shows sleeping in view`() {
            given(sleepDataSource.getCurrent()).willReturn(Single.just(awake), Single.just(sleeping))
            viewModel.processIntents(Observable.just(WidgetIntent.ToggleSleepIntent))
            inOrder(sleepDataSource) {
                verify(sleepDataSource).getCurrent()
                verify(sleepDataSource).insert(any())
                verify(sleepDataSource).getCurrent()
            }
            testObserver.assertValueAt(0) { it.isSleeping }
        }

        @Test
        fun `toggle from sleeping shows awake in view`() {
            given(sleepDataSource.getCurrent()).willReturn(Single.just(sleeping), Single.just(awake))
            viewModel.processIntents(Observable.just(WidgetIntent.ToggleSleepIntent))
            inOrder(sleepDataSource) {
                verify(sleepDataSource).getCurrent()
                verify(sleepDataSource).update(any())
                verify(sleepDataSource).getCurrent()
            }
            testObserver.assertValueAt(0) { !it.isSleeping }
        }
    }


}