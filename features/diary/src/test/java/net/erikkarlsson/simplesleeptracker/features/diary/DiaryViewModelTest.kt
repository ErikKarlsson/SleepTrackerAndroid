package net.erikkarlsson.simplesleeptracker.features.diary

import androidx.paging.PagedList
import com.airbnb.mvrx.Success
import com.airbnb.mvrx.withState
import com.nhaarman.mockitokotlin2.given
import com.nhaarman.mockitokotlin2.mock
import junit.framework.Assert.assertEquals
import kotlinx.coroutines.flow.flowOf
import net.erikkarlsson.simplesleeptracker.domain.SleepDataSource
import net.erikkarlsson.simplesleeptracker.domain.StatisticsDataSource
import net.erikkarlsson.simplesleeptracker.domain.entity.Sleep
import net.erikkarlsson.simplesleeptracker.domain.entity.SleepDiary
import net.erikkarlsson.simplesleeptracker.features.diary.domain.GetSleepDiaryTask
import net.erikkarlsson.simplesleeptracker.testutil.RxImmediateSchedulerRule
import net.erikkarlsson.simplesleeptracker.testutil.TestCoroutineRule
import org.junit.Rule
import org.junit.Test

class DiaryViewModelTest {

    @get:Rule
    val testCoroutineRule = TestCoroutineRule()

    @get:Rule
    var testSchedulerRule = RxImmediateSchedulerRule()

    val sleepRepository: SleepDataSource = mock()
    val statisticsRepository: StatisticsDataSource = mock()
    val sleepPaged: PagedList<Sleep> = mock()
    val getSleepDiaryTask = GetSleepDiaryTask(sleepRepository, statisticsRepository)

    @Test
    fun `load sleep shows sleep in view`() = testCoroutineRule.runBlockingTest {
        given(sleepRepository.getSleepPaged()).willReturn(flowOf(sleepPaged))
        given(statisticsRepository.getSleepCountYearMonth()).willReturn(flowOf(emptyList()))

        val viewModel = createViewModel()

        withState(viewModel) {
            assertEquals(it, DiaryState(Success(SleepDiary(sleepPaged, emptyList()))))
        }
    }

    private fun createViewModel(): DiaryViewModel {
        return DiaryViewModel(DiaryState(), getSleepDiaryTask)
    }

}
