package net.erikkarlsson.simplesleeptracker.features.statistics.item

import com.airbnb.mvrx.Success
import com.airbnb.mvrx.withState
import com.nhaarman.mockitokotlin2.given
import com.nhaarman.mockitokotlin2.mock
import junit.framework.Assert.assertEquals
import kotlinx.coroutines.flow.flowOf
import net.erikkarlsson.simplesleeptracker.domain.StatisticsDataSource
import net.erikkarlsson.simplesleeptracker.domain.entity.DateRange
import net.erikkarlsson.simplesleeptracker.domain.entity.StatisticComparison
import net.erikkarlsson.simplesleeptracker.domain.entity.Statistics
import net.erikkarlsson.simplesleeptracker.features.statistics.StatisticsFilter
import net.erikkarlsson.simplesleeptracker.features.statistics.domain.StatisticComparisonTask
import net.erikkarlsson.simplesleeptracker.features.statistics.domain.StatisticOverallTask
import net.erikkarlsson.simplesleeptracker.testutil.RxImmediateSchedulerRule
import net.erikkarlsson.simplesleeptracker.testutil.TestCoroutineRule
import net.erikkarlsson.simplesleeptracker.testutil.mockStatistics
import org.junit.Rule
import org.junit.Test
import org.threeten.bp.LocalDate

class StatisticsItemViewModelTest {

    @get:Rule
    val testCoroutineRule = TestCoroutineRule()

    @get:Rule
    var testSchedulerRule = RxImmediateSchedulerRule()

    val statisticsDataSource: StatisticsDataSource = mock()

    val statisticOverallTask = StatisticOverallTask(statisticsDataSource)
    val statisticComparisonTask = StatisticComparisonTask(statisticsDataSource)

    private fun createViewModel(): StatisticsItemViewModel {
        return StatisticsItemViewModel(StatisticsItemState(),
                statisticOverallTask, statisticComparisonTask)
    }

    @Test
    fun `load overall statistics shows statistics in view`() = testCoroutineRule.runBlockingTest {
        val statistics = mockStatistics(4)
        given(statisticsDataSource.getStatistics()).willReturn(flowOf(statistics))

        val viewModel = createViewModel()

        val dateRangePair = DateRange.empty() to DateRange.empty()
        val expectedStatisticComparison = StatisticComparison(statistics, Statistics.empty())

        viewModel.loadStatistics(dateRangePair, StatisticsFilter.OVERALL)

        withState(viewModel) {
            assertEquals(it, StatisticsItemState(Success(expectedStatisticComparison)))
        }
    }

    @Test
    fun `load comparison shows statistics in view`() = testCoroutineRule.runBlockingTest {
        val now = LocalDate.now()
        val dateRangeFirst = DateRange(now, now.plusWeeks(1))
        val dateRangeSecond = DateRange(now.plusWeeks(1), now.plusWeeks(2))
        val statisticsFirst = mockStatistics(4)
        val statisticsSecond = mockStatistics(5)

        given(statisticsDataSource.getStatistics(dateRangeFirst))
                .willReturn(flowOf(statisticsFirst))

        given(statisticsDataSource.getStatistics(dateRangeSecond))
                .willReturn(flowOf(statisticsSecond))

        val viewModel = createViewModel()

        val dateRangePair = dateRangeFirst to dateRangeSecond
        val expectedStatisticComparison = StatisticComparison(statisticsFirst, statisticsSecond)

        viewModel.loadStatistics(dateRangePair, StatisticsFilter.WEEK)

        withState(viewModel) {
            assertEquals(it, StatisticsItemState(Success(expectedStatisticComparison)))
        }
    }

}
