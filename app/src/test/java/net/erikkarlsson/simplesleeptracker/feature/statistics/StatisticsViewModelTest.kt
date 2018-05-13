package net.erikkarlsson.simplesleeptracker.feature.statistics

import android.arch.lifecycle.Observer
import com.google.common.collect.ImmutableList
import com.nhaarman.mockito_kotlin.*
import io.reactivex.Completable
import io.reactivex.Observable
import net.erikkarlsson.simplesleeptracker.domain.SleepDataSource
import net.erikkarlsson.simplesleeptracker.domain.ToggleSleepTaskTest
import net.erikkarlsson.simplesleeptracker.domain.entity.Sleep
import net.erikkarlsson.simplesleeptracker.domain.entity.StatisticComparison
import net.erikkarlsson.simplesleeptracker.domain.entity.Statistics
import net.erikkarlsson.simplesleeptracker.domain.task.StatisticComparisonOverallTask
import net.erikkarlsson.simplesleeptracker.domain.task.StatisticComparisonWeekTask
import net.erikkarlsson.simplesleeptracker.domain.task.ToggleSleepTask
import net.erikkarlsson.simplesleeptracker.testutil.InstantTaskExecutorExtension
import net.erikkarlsson.simplesleeptracker.testutil.RxImmediateSchedulerExtension
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
    val statisticComparisonWeekTask: StatisticComparisonWeekTask = mock()
    val statisticComparisonOverallTask: StatisticComparisonOverallTask = mock()

    @BeforeEach
    fun setUp() {
        reset(sleepRepository, observer, toggleSleepTask, statisticComparisonWeekTask, statisticComparisonOverallTask)
    }

    @Test
    fun `load statistics shows statistics in view`() {
        val expectedStatisticComparison = StatisticComparison(Statistics.empty(), Statistics.empty())

        given(statisticComparisonOverallTask.execute()).willReturn(Observable.just(expectedStatisticComparison))
        given(sleepRepository.getCurrent()).willReturn(Observable.just(Sleep.empty()))
        given(sleepRepository.getSleep()).willReturn(Observable.just(ImmutableList.of()))

        val viewModel = createViewModel()

        viewModel.state().observeForever(observer)

        verify(observer).onChanged(StatisticsState(false, expectedStatisticComparison, ImmutableList.of(), StatisticsFilter.OVERALL))
    }

    /**
     * See [ToggleSleepTaskTest] for extensive coverage of toggle cases.
     */
    @Test
    fun `clicking toggle sleep button toggles sleep`() {
        given(toggleSleepTask.execute(any())).willReturn(Completable.complete())
        given(statisticComparisonWeekTask.execute()).willReturn(Observable.just(StatisticComparison.empty()))
        given(sleepRepository.getSleep()).willReturn(Observable.just(ImmutableList.of()))
        given(sleepRepository.getCurrent()).willReturn(Observable.just(Sleep.empty()))

        val viewModel = createViewModel()

        viewModel.dispatch(ToggleSleepClicked)

        verify(toggleSleepTask).execute(any())
    }

    private fun createViewModel(): StatisticsViewModel {
        val sleepSubscription = SleepSubscription(sleepRepository)
        val statisticsSubscription = StatisticsSubscription(statisticComparisonOverallTask, statisticComparisonWeekTask)
        val statisticsComponent = StatisticsComponent(toggleSleepTask, sleepSubscription, statisticsSubscription)
        return StatisticsViewModel(statisticsComponent)
    }
}