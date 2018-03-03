package net.erikkarlsson.simplesleeptracker.statisticselm

import android.arch.lifecycle.Observer
import com.nhaarman.mockito_kotlin.given
import com.nhaarman.mockito_kotlin.inOrder
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.reset
import io.reactivex.Single
import net.erikkarlsson.simplesleeptracker.domain.Statistics
import net.erikkarlsson.simplesleeptracker.domain.StatisticsDataSource
import net.erikkarlsson.simplesleeptracker.statisticselm.StatisticsMsg.InitialIntent
import net.erikkarlsson.simplesleeptracker.statisticselm.task.LoadStatistics
import net.erikkarlsson.simplesleeptracker.util.ImmediateSchedulerProvider
import net.erikkarlsson.simplesleeptracker.util.InstantTaskExecutorExtension
import net.erikkarlsson.simplesleeptracker.util.RxImmediateSchedulerExtension
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.extension.ExtendWith

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ExtendWith(InstantTaskExecutorExtension::class, RxImmediateSchedulerExtension::class)
class StatisticsElmViewModelTest {

    lateinit var viewModel: StatisticsElmViewModel

    val schedulerProvider = ImmediateSchedulerProvider()

    val statisticsRepository: StatisticsDataSource = mock()
    val observer: Observer<StatisticsState> = mock()

    @BeforeEach
    fun setUp() {
        reset(statisticsRepository, observer)
        val loadStatistics = LoadStatistics(statisticsRepository, schedulerProvider)
        val statisticsComponent = StatisticsComponent(loadStatistics)
        viewModel = StatisticsElmViewModel(statisticsComponent)
        viewModel.state().observeForever(observer)
    }

    @Test
    fun `load statistics shows statistics in view`() {
        val expectedStatistics = Statistics(2.2f)
        given(statisticsRepository.getStatistics()).willReturn(Single.just(expectedStatistics))
        viewModel.dispatch(InitialIntent)

        inOrder(observer) {
            verify(observer).onChanged(StatisticsState.empty())
            verify(observer).onChanged(StatisticsState(expectedStatistics))
        }
    }
}