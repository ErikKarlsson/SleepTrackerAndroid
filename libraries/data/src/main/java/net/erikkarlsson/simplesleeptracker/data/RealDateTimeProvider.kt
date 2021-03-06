package net.erikkarlsson.simplesleeptracker.data

import net.erikkarlsson.simplesleeptracker.domain.DateTimeProvider
import org.threeten.bp.OffsetDateTime
import javax.inject.Inject

class RealDateTimeProvider @Inject constructor() : DateTimeProvider {
    override fun now(): OffsetDateTime {
        return OffsetDateTime.now()
    }

}
