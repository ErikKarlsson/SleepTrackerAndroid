package net.erikkarlsson.simplesleeptracker.feature.statistics.item

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.nhaarman.mockito_kotlin.given
import com.nhaarman.mockito_kotlin.inOrder
import com.nhaarman.mockito_kotlin.mock
import io.reactivex.Observable
import net.erikkarlsson.simplesleeptracker.base.mockStatistics
import net.erikkarlsson.simplesleeptracker.domain.StatisticsDataSource
import net.erikkarlsson.simplesleeptracker.domain.entity.DateRange
import net.erikkarlsson.simplesleeptracker.domain.entity.StatisticComparison
import net.erikkarlsson.simplesleeptracker.domain.entity.Statistics
import net.erikkarlsson.simplesleeptracker.feature.statistics.StatisticsFilter
import net.erikkarlsson.simplesleeptracker.feature.statistics.domain.StatisticComparisonTask
import net.erikkarlsson.simplesleeptracker.feature.statistics.domain.StatisticOverallTask
import net.erikkarlsson.simplesleeptracker.testutil.RxImmediateSchedulerRule
import org.junit.Rule
import org.junit.Test
import org.threeten.bp.LocalDate

class StatisticsItemViewModelTest {

    @get:Rule
    var testSchedulerRule = RxImmediateSchedulerRule()

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    val statisticsDataSource: StatisticsDataSource = mock()
    val observer: Observer<StatisticsItemState> = mock()

    val statisticOverallTask = StatisticOverallTask(statisticsDataSource)
    val statisticComparisonTask = StatisticComparisonTask(statisticsDataSource)

    private fun createViewModel(): StatisticsItemViewModel {
        val statisticsSubscription = StatisticsSubscription(statisticOverallTask, statisticComparisonTask)
        val statisticsItemComponent = StatisticsItemComponent(statisticsSubscription)
        return StatisticsItemViewModel(statisticsItemComponent)
    }

    @Test
    fun `load overall statistics shows statistics in view`() {
        val statistics = mockStatistics(4)
        given(statisticsDataSource.getStatistics()).willReturn(Observable.just(statistics))

        val viewModel = createViewModel()

        viewModel.state().observeForever(observer)

        val dateRangePair = DateRange.empty() to DateRange.empty()
        val expectedStatisticComparison = StatisticComparison(statistics, Statistics.empty())

        viewModel.dispatch(LoadStatistics(dateRangePair, StatisticsFilter.OVERALL))

        inOrder(observer) {
            verify(observer).onChanged(StatisticsItemState.empty())
            verify(observer).onChanged(StatisticsItemState(false,
                    expectedStatisticComparison, StatisticsFilter.OVERALL, dateRangePair))
            verifyNoMoreInteractions()
        }
    }

    @Test
    fun `load comparison shows statistics in view`() {
        val now = LocalDate.now()
        val dateRangeFirst = DateRange(now, now.plusWeeks(1))
        val dateRangeSecond = DateRange(now.plusWeeks(1), now.plusWeeks(2))
        val statisticsFirst = mockStatistics(4)
        val statisticsSecond = mockStatistics(5)

        given(statisticsDataSource.getStatistics(dateRangeFirst))
                .willReturn(Observable.just(statisticsFirst))

        given(statisticsDataSource.getStatistics(dateRangeSecond))
                .willReturn(Observable.just(statisticsSecond))

        val viewModel = createViewModel()

        viewModel.state().observeForever(observer)

        val dateRangePair = dateRangeFirst to dateRangeSecond
        val expectedStatisticComparison = StatisticComparison(statisticsFirst, statisticsSecond)

        viewModel.dispatch(LoadStatistics(dateRangePair, StatisticsFilter.WEEK))

        inOrder(observer) {
            verify(observer).onChanged(StatisticsItemState.empty())
            verify(observer).onChanged(StatisticsItemState(false,
                    expectedStatisticComparison, StatisticsFilter.WEEK, dateRangePair))
            verifyNoMoreInteractions()
        }
    }

}