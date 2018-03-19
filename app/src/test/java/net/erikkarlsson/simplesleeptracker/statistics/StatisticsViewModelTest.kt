package net.erikkarlsson.simplesleeptracker.statistics

import android.arch.lifecycle.Observer
import com.nhaarman.mockito_kotlin.*
import io.reactivex.Completable
import io.reactivex.Observable
import net.erikkarlsson.simplesleeptracker.domain.*
import net.erikkarlsson.simplesleeptracker.util.InstantTaskExecutorExtension
import net.erikkarlsson.simplesleeptracker.util.RxImmediateSchedulerExtension
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.extension.ExtendWith

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ExtendWith(InstantTaskExecutorExtension::class, RxImmediateSchedulerExtension::class)
class StatisticsViewModelTest {

    val statisticsRepository: StatisticsDataSource = mock()
    val sleepRepository: SleepDataSource = mock()
    val observer: Observer<StatisticsState> = mock()
    val toggleSleepTask: ToggleSleepTask = mock()

    @BeforeEach
    fun setUp() {
        reset(statisticsRepository, observer, toggleSleepTask)
    }

    @Test
    fun `load statistics shows statistics in view`() {
        val expectedStatistics = StatisticComparison(Statistics.empty(), Statistics.empty())

        given(statisticsRepository.getStatisticComparison(any(), any(), any(), any()))
            .willReturn(Observable.just(expectedStatistics))
        given(sleepRepository.getCurrent()).willReturn(Observable.just(Sleep.empty()))
        given(sleepRepository.getSleep()).willReturn(Observable.just(emptyList()))

        val viewModel = createViewModel()
        viewModel.state().observeForever(observer)

        verify(observer).onChanged(StatisticsState(expectedStatistics, emptyList()))
    }

    @Nested
    inner class ToggleCases {

        /**
         * See [ToggleSleepTaskTest] for extensive coverage of toggle cases.
         */

        @Test
        fun `clicking toggle sleep button toggles sleep`() {
            given(toggleSleepTask.execute()).willReturn(Completable.complete())
            given(statisticsRepository.getStatisticComparison(any(), any(), any(), any()))
                .willReturn(Observable.just(StatisticComparison.empty()))
            given(sleepRepository.getSleep()).willReturn(Observable.just(emptyList()))
            val viewModel = createViewModel()
            viewModel.dispatch(ToggleSleepClicked)
            verify(toggleSleepTask).execute()
        }

    }

    private fun createViewModel(): StatisticsViewModel {
        val sleepSubscription = SleepSubscription(sleepRepository)
        val statisticsSubscription = StatisticsSubscription(statisticsRepository)
        val statisticsComponent = StatisticsComponent(toggleSleepTask, sleepSubscription, statisticsSubscription)
        return StatisticsViewModel(statisticsComponent)
    }
}