package net.erikkarlsson.simplesleeptracker.statistics

import android.arch.lifecycle.Observer
import com.nhaarman.mockito_kotlin.given
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.reset
import com.nhaarman.mockito_kotlin.verify
import io.reactivex.Completable
import io.reactivex.Observable
import net.erikkarlsson.simplesleeptracker.domain.*
import net.erikkarlsson.simplesleeptracker.util.InstantTaskExecutorExtension
import net.erikkarlsson.simplesleeptracker.util.RxImmediateSchedulerExtension
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.extension.ExtendWith

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ExtendWith(InstantTaskExecutorExtension::class, RxImmediateSchedulerExtension::class)
class StatisticsViewModelTest {

    val sleepRepository: SleepDataSource = mock()
    val observer: Observer<StatisticsState> = mock()
    val toggleSleepTask: ToggleSleepTask = mock()
    val statisticComparisonTask: StatisticComparisonTask = mock()

    @BeforeEach
    fun setUp() {
        reset(sleepRepository, observer, toggleSleepTask, statisticComparisonTask)
    }

    @Test
    fun `load statistics shows statistics in view`() {
        val expectedStatisticComparison = StatisticComparison(Statistics.empty(), Statistics.empty())

        given(statisticComparisonTask.execute()).willReturn(Observable.just(expectedStatisticComparison))
        given(sleepRepository.getCurrent()).willReturn(Observable.just(Sleep.empty()))
        given(sleepRepository.getSleep()).willReturn(Observable.just(emptyList()))

        val viewModel = createViewModel()

        viewModel.state().observeForever(observer)

        verify(observer).onChanged(StatisticsState(false, expectedStatisticComparison, emptyList()))
    }

    /**
     * See [ToggleSleepTaskTest] for extensive coverage of toggle cases.
     */
    @Test
    fun `clicking toggle sleep button toggles sleep`() {
        given(toggleSleepTask.execute()).willReturn(Completable.complete())
        given(statisticComparisonTask.execute()).willReturn(Observable.just(StatisticComparison.empty()))
        given(sleepRepository.getSleep()).willReturn(Observable.just(emptyList()))
        given(sleepRepository.getCurrent()).willReturn(Observable.just(Sleep.empty()))

        val viewModel = createViewModel()

        viewModel.dispatch(ToggleSleepClicked)

        verify(toggleSleepTask).execute()
    }

    private fun createViewModel(): StatisticsViewModel {
        val sleepSubscription = SleepSubscription(sleepRepository)
        val currentSleepSubscription = CurrentSleepSubscription(sleepRepository)
        val statisticsSubscription = StatisticsSubscription(statisticComparisonTask)
        val statisticsComponent = StatisticsComponent(toggleSleepTask, sleepSubscription,
                currentSleepSubscription, statisticsSubscription)
        return StatisticsViewModel(statisticsComponent)
    }
}