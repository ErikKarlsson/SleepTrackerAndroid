package net.erikkarlsson.simplesleeptracker.statistics

import android.arch.core.executor.testing.InstantTaskExecutorRule
import android.arch.lifecycle.Observer
import com.nhaarman.mockito_kotlin.given
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.verify
import io.reactivex.Observable
import io.reactivex.Single
import net.erikkarlsson.simplesleeptracker.domain.Statistics
import net.erikkarlsson.simplesleeptracker.domain.StatisticsDataSource
import net.erikkarlsson.simplesleeptracker.statistics.processor.LoadStatistics
import net.erikkarlsson.simplesleeptracker.util.ImmediateSchedulerProvider
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class StatisticsViewModelTest {

    @Rule
    @JvmField
    val rule = InstantTaskExecutorRule()

    lateinit var viewModel: StatisticsViewModel

    val schedulerProvider = ImmediateSchedulerProvider()

    val statisticsRepository: StatisticsDataSource = mock()
    val observer: Observer<StatisticsViewState> = mock()

    @Before
    fun setUp() {
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