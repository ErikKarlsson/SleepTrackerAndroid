package net.erikkarlsson.simplesleeptracker

import net.erikkarlsson.simplesleeptracker.base.MockDateTimeProvider
import net.erikkarlsson.simplesleeptracker.data.SleepRepository
import net.erikkarlsson.simplesleeptracker.di.DaggerTestComponent
import net.erikkarlsson.simplesleeptracker.di.TestComponent
import net.erikkarlsson.simplesleeptracker.domain.entity.Sleep
import org.threeten.bp.OffsetDateTime
import timber.log.Timber
import javax.inject.Inject

class TestApp : App(), TestableApplication {
    private lateinit var testComponent: TestComponent

    @Inject lateinit var sleepRepository: SleepRepository
    @Inject lateinit var dateTimeProvider: MockDateTimeProvider

    override fun reInitDependencyInjection() {
        Timber.d("reInitDependencyInjection")
        testComponent = DaggerTestComponent.builder().application(this).build()
        testComponent.inject(this)
    }

    override fun clearDataBetweenTests() {
        sleepRepository.deleteAll()
    }

    override fun insertSleep(sleep: Sleep) {
        sleepRepository.insert(sleep)
    }

    override fun mockDateTimeNow(offsetDateTime: OffsetDateTime) {
        dateTimeProvider.nowValue = offsetDateTime
    }

}
