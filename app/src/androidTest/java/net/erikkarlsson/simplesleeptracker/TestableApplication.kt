package net.erikkarlsson.simplesleeptracker

import net.erikkarlsson.simplesleeptracker.domain.entity.Sleep
import org.threeten.bp.OffsetDateTime

interface TestableApplication {
    fun reInitDependencyInjection()
    fun clearDataBetweenTests()
    fun insertSleep(sleep: Sleep)
    fun mockDateTimeNow(offsetDateTime: OffsetDateTime)
}