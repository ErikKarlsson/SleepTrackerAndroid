package net.erikkarlsson.simplesleeptracker.feature.diary

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import androidx.paging.PagedList
import com.nhaarman.mockito_kotlin.given
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.verify
import io.reactivex.Observable
import net.erikkarlsson.simplesleeptracker.data.statistics.StatisticsRepository
import net.erikkarlsson.simplesleeptracker.domain.SleepDataSource
import net.erikkarlsson.simplesleeptracker.domain.entity.Sleep
import net.erikkarlsson.simplesleeptracker.domain.entity.SleepDiary
import net.erikkarlsson.simplesleeptracker.feature.diary.domain.GetSleepDiaryTask
import net.erikkarlsson.simplesleeptracker.testutil.RxImmediateSchedulerRule
import org.junit.Rule
import org.junit.Test

class DiaryViewModelTest {

    @get:Rule
    var testSchedulerRule = RxImmediateSchedulerRule()

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    val sleepRepository: SleepDataSource = mock()
    val statisticsRepository: StatisticsRepository = mock()
    val observer: Observer<DiaryState> = mock()
    val sleepPaged: PagedList<Sleep> = mock()
    val getSleepDiaryTask = GetSleepDiaryTask(sleepRepository, statisticsRepository)

    @Test
    fun `load sleep shows sleep in view`() {
        given(sleepRepository.getSleepPaged()).willReturn(Observable.just(sleepPaged))
        given(statisticsRepository.getSleepCountYearMonth()).willReturn(Observable.just(emptyList()))

        val viewModel = createViewModel()
        viewModel.state().observeForever(observer)
        verify(observer).onChanged(DiaryState(SleepDiary(sleepPaged, emptyList())))
    }

    private fun createViewModel(): DiaryViewModel {
        val sleepSubscription = SleepSubscription(getSleepDiaryTask)
        val diaryComponent = DiaryComponent(sleepSubscription)
        return DiaryViewModel(diaryComponent)
    }

}