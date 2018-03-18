package net.erikkarlsson.simplesleeptracker.sleepappwidget

import android.arch.lifecycle.Observer
import com.nhaarman.mockito_kotlin.given
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.reset
import com.nhaarman.mockito_kotlin.verify
import io.reactivex.Observable
import io.reactivex.Single
import net.erikkarlsson.simplesleeptracker.base.MockDateTimeProvider
import net.erikkarlsson.simplesleeptracker.domain.Sleep
import net.erikkarlsson.simplesleeptracker.domain.SleepDataSource
import net.erikkarlsson.simplesleeptracker.domain.ToggleSleepTask
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

    val dateTimeProvider = MockDateTimeProvider()
    val sleepRepository: SleepDataSource = mock()
    val observer: Observer<WidgetState> = mock()

    @BeforeEach
    fun setUp() {
        reset(sleepRepository, observer)
        dateTimeProvider.reset()
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
    inner class ToggleCases {

        @Test
        fun `toggle from empty shows sleeping in view`() {
            given(sleepRepository.getCurrentSingle()).willReturn(Single.just(Sleep.empty()))
            given(sleepRepository.getCurrent()).willReturn(Observable.just(Sleep.empty()))
            val now = dateTimeProvider.mockDateTime()
            val sleeping = Sleep(fromDate = now)
            val viewModel = createViewModel()
            viewModel.dispatch(ToggleSleepClicked)
            verify(sleepRepository).insert(sleeping)
        }

        @Test
        fun `toggle from awake shows sleeping in view`() {
            val awake = Sleep(fromDate = OffsetDateTime.now(), toDate = OffsetDateTime.now().plusDays(1))
            given(sleepRepository.getCurrentSingle()).willReturn(Single.just(awake))
            given(sleepRepository.getCurrent()).willReturn(Observable.just(Sleep.empty()))
            val now = dateTimeProvider.mockDateTime()
            val sleeping = Sleep(fromDate = now)
            val viewModel = createViewModel()
            viewModel.dispatch(ToggleSleepClicked)
            verify(sleepRepository).insert(sleeping)
        }

        @Test
        fun `toggle from sleeping shows awake in view`() {
            val now = dateTimeProvider.mockDateTime()
            val anHourAgo = now.minusHours(1)
            val sleeping = Sleep(fromDate = anHourAgo)
            val awake = Sleep(fromDate = anHourAgo, toDate = now)
            given(sleepRepository.getCurrentSingle()).willReturn(Single.just(sleeping))
            given(sleepRepository.getCurrent()).willReturn(Observable.just(Sleep.empty()))
            val viewModel = createViewModel()
            viewModel.dispatch(ToggleSleepClicked)
            verify(sleepRepository).update(awake)
        }
    }

    private fun createViewModel(): SleepAppWidgetViewModel {
        val toggleSleep = ToggleSleepTask(sleepRepository, dateTimeProvider)
        val sleepSubscription = SleepSubscription(sleepRepository)
        val widgetComponent = AppWidgetComponent(toggleSleep, sleepSubscription)
        return SleepAppWidgetViewModel(widgetComponent)
    }

}