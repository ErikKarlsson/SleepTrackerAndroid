package net.erikkarlsson.simplesleeptracker.features.diary

import androidx.paging.PagedList
import com.nhaarman.mockitokotlin2.given
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import kotlinx.coroutines.flow.flowOf
import net.erikkarlsson.simplesleeptracker.domain.SleepDataSource
import net.erikkarlsson.simplesleeptracker.domain.StatisticsDataSource
import net.erikkarlsson.simplesleeptracker.domain.entity.Sleep
import net.erikkarlsson.simplesleeptracker.domain.entity.SleepDiary
import net.erikkarlsson.simplesleeptracker.features.diary.domain.GetSleepDiaryTask
import net.erikkarlsson.simplesleeptracker.testutil.TestCoroutineRule
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

class DiaryViewModelTest {

    @get:Rule
    val testCoroutineRule = TestCoroutineRule()

    val sleepRepository: SleepDataSource = mock()
    val statisticsRepository: StatisticsDataSource = mock()
    val sleepPaged: PagedList<Sleep> = mock()
    val getSleepDiaryTask = GetSleepDiaryTask(sleepRepository, statisticsRepository)

    @Test
    fun `load sleep shows sleep in view`() = testCoroutineRule.runBlockingTest {
        given(sleepRepository.getSleepPaged()).willReturn(flowOf(sleepPaged))
        given(statisticsRepository.getSleepCountYearMonth()).willReturn(flowOf(emptyList()))

        val viewModel = createViewModel()

        assertEquals(viewModel.currentState(), DiaryState(SleepDiary(sleepPaged, emptyList())))
    }

    private fun createViewModel(): DiaryViewModel {
        return DiaryViewModel(getSleepDiaryTask)
    }

}
