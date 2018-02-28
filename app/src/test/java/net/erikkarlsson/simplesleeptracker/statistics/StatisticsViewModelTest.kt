package net.erikkarlsson.simplesleeptracker.statistics

import android.arch.lifecycle.Observer
import com.nhaarman.mockito_kotlin.given
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.reset
import com.nhaarman.mockito_kotlin.verify
import io.reactivex.Observable
import io.reactivex.Single
import net.erikkarlsson.simplesleeptracker.domain.Statistics
import net.erikkarlsson.simplesleeptracker.domain.StatisticsDataSource
import net.erikkarlsson.simplesleeptracker.statistics.processor.LoadStatistics
import net.erikkarlsson.simplesleeptracker.util.ImmediateSchedulerProvider
import net.erikkarlsson.simplesleeptracker.util.InstantTaskExecutorExtension
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.extension.ExtendWith

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ExtendWith(InstantTaskExecutorExtension::class)
class StatisticsViewModelTest {

    lateinit var viewModel: StatisticsViewModel

    val schedulerProvider = ImmediateSchedulerProvider()

    val statisticsRepository: StatisticsDataSource = mock()
    val observer: Observer<StatisticsViewState> = mock()

    @BeforeEach
    fun setUp() {
        reset(statisticsRepository, observer)
        val loadStatistics = LoadStatistics(statisticsRepository, schedulerProvider)
        val statisticsProcessorHolder = StatisticsProcessorHolder(loadStatistics)

        viewModel = StatisticsViewModel(statisticsProcessorHolder, schedulerProvider)
        viewModel.statesLiveData().observeForever(observer)
    }

    @Test
    fun `load statistics shows statistics in view`() {
        val expectedStatistics = Statistics(2.2f)
        given(statisticsRepository.getStatistics()).willReturn(Single.just(expectedStatistics))
        viewModel.processIntents(Observable.just(StatisticsIntent.InitialIntent))
        verify(statisticsRepository).getStatistics()
        verify(observer).onChanged(StatisticsViewState(expectedStatistics))
    }
}