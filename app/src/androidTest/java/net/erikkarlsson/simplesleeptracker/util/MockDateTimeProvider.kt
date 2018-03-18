package net.erikkarlsson.simplesleeptracker.util

import net.erikkarlsson.simplesleeptracker.domain.DateTimeProvider
import org.threeten.bp.OffsetDateTime
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MockDateTimeProvider @Inject constructor(): DateTimeProvider {

    var mockNowValue: OffsetDateTime? = null

    override fun now(): OffsetDateTime = mockNowValue ?: OffsetDateTime.now()
}