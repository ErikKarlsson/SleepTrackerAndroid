package net.erikkarlsson.simplesleeptracker.base

import net.erikkarlsson.simplesleeptracker.domain.DateTimeProvider
import org.threeten.bp.OffsetDateTime
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MockDateTimeProvider @Inject constructor(): DateTimeProvider {

    var nowValue: OffsetDateTime? = null

    fun mockDateTime(): OffsetDateTime {
        val now = OffsetDateTime.now()
        nowValue = now
        return now
    }

    fun reset() {
        nowValue = null
    }

    override fun now(): OffsetDateTime = nowValue ?: OffsetDateTime.now()
}