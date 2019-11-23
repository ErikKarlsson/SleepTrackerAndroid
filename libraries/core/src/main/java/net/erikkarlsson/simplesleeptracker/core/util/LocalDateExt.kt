package net.erikkarlsson.simplesleeptracker.core.util

import org.threeten.bp.LocalDate
import org.threeten.bp.temporal.IsoFields

val LocalDate.weekOfWeekBasedYear: Int
    get() {
        return this.get(IsoFields.WEEK_OF_WEEK_BASED_YEAR)
    }


