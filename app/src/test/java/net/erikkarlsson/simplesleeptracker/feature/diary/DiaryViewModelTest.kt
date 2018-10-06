package net.erikkarlsson.simplesleeptracker.feature.diary

import androidx.lifecycle.Observer
import androidx.paging.PagedList
import com.nhaarman.mockito_kotlin.given
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.reset
import com.nhaarman.mockito_kotlin.verify
import io.reactivex.Observable
import net.erikkarlsson.simplesleeptracker.domain.SleepDataSource
import net.erikkarlsson.simplesleeptracker.domain.entity.Sleep
import net.erikkarlsson.simplesleeptracker.testutil.InstantTaskExecutorExtension
import net.erikkarlsson.simplesleeptracker.testutil.RxImmediateSchedulerExtension
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.extension.ExtendWith

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ExtendWith(InstantTaskExecutorExtension::class, RxImmediateSchedulerExtension::class)
class DiaryViewModelTest {

    val sleepRepository: SleepDataSource = mock()
    val observer: Observer<DiaryState> = mock()
    val sleepPaged: PagedList<Sleep> = mock()

    @BeforeEach
    fun setUp() {
        reset(sleepRepository)
    }

    @Test
    fun `load sleep shows sleep in view`() {
        given(sleepRepository.getSleepPaged()).willReturn(Observable.just(sleepPaged))

        val viewModel = createViewModel()
        viewModel.state().observeForever(observer)
        verify(observer).onChanged(DiaryState(sleepPaged))
    }

    private fun createViewModel(): DiaryViewModel {
        val sleepSubscription = SleepSubscription(sleepRepository)
        val diaryComponent = DiaryComponent(sleepSubscription)
        return DiaryViewModel(diaryComponent)
    }

}