package net.erikkarlsson.simplesleeptracker

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import net.erikkarlsson.simplesleeptracker.data.sleep.SleepRepository
import net.erikkarlsson.simplesleeptracker.di.DaggerTestComponent
import net.erikkarlsson.simplesleeptracker.domain.entity.Sleep
import net.erikkarlsson.simplesleeptracker.testutil.MockDateTimeProvider
import org.threeten.bp.OffsetDateTime
import javax.inject.Inject

class TestApp : App(), TestableApplication {
    @Inject lateinit var sleepRepository: SleepRepository
    @Inject lateinit var dateTimeProvider: MockDateTimeProvider

    override fun reInitDependencyInjection() {
        DaggerTestComponent.builder().application(this).build().inject(this)
    }

    override fun clearDataBetweenTests() {
        GlobalScope.launch {
            sleepRepository.deleteAll()
        }
    }

    override fun insertSleep(sleep: Sleep) {
        GlobalScope.launch {
            sleepRepository.insert(sleep)
        }
    }

    override fun mockDateTimeNow(offsetDateTime: OffsetDateTime) {
        dateTimeProvider.nowValue = offsetDateTime
    }

}
