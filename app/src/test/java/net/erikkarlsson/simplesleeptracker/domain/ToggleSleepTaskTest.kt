package net.erikkarlsson.simplesleeptracker.domain

import com.nhaarman.mockito_kotlin.given
import com.nhaarman.mockito_kotlin.inOrder
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.reset
import io.reactivex.Single
import net.erikkarlsson.simplesleeptracker.base.MockDateTimeProvider
import net.erikkarlsson.simplesleeptracker.util.InstantTaskExecutorExtension
import net.erikkarlsson.simplesleeptracker.util.RxImmediateSchedulerExtension
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.extension.ExtendWith

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ExtendWith(InstantTaskExecutorExtension::class, RxImmediateSchedulerExtension::class)
class ToggleSleepTaskTest {

    val dateTimeProvider = MockDateTimeProvider()
    val sleepRepository: SleepDataSource = mock()
    val toggleSleepTask = ToggleSleepTask(sleepRepository, dateTimeProvider)

    @BeforeEach
    fun setUp() {
        reset(sleepRepository)
        dateTimeProvider.reset()
    }

    @Test
    fun `toggle from empty inserts sleep`() {
        val now = dateTimeProvider.mockDateTime()
        val sleeping = Sleep(fromDate = now)
        given(sleepRepository.getCurrentSingle()).willReturn(Single.just(Sleep.empty()))
        val observer = toggleSleepTask.execute().test()
        observer.assertComplete()

        inOrder(sleepRepository) {
            verify(sleepRepository).getCurrentSingle()
            verify(sleepRepository).insert(sleeping)
            verifyNoMoreInteractions()
        }
    }

    @Test
    fun `toggle from sleeping updates to awake`() {
        val now = dateTimeProvider.mockDateTime()
        val anHourAgo = now.minusHours(1)
        val sleeping = Sleep(fromDate = anHourAgo)
        val awake = Sleep(fromDate = anHourAgo, toDate = now)
        given(sleepRepository.getCurrentSingle()).willReturn(Single.just(sleeping))
        val observer = toggleSleepTask.execute().test()
        observer.assertComplete()

        inOrder(sleepRepository) {
            verify(sleepRepository).getCurrentSingle()
            verify(sleepRepository).update(awake)
            verifyNoMoreInteractions()
        }
    }

    @Test
    fun `toggle from awake inserts sleeping`() {
        val now = dateTimeProvider.mockDateTime()
        val sleeping = Sleep(fromDate = now)
        val awake = Sleep(fromDate = now, toDate = now.plusDays(1))
        given(sleepRepository.getCurrentSingle()).willReturn(Single.just(awake))
        val observer = toggleSleepTask.execute().test()
        observer.assertComplete()

        inOrder(sleepRepository) {
            verify(sleepRepository).getCurrentSingle()
            verify(sleepRepository).insert(sleeping)
            verifyNoMoreInteractions()
        }
    }
}