package net.erikkarlsson.simplesleeptracker.core.util

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import org.threeten.bp.LocalDate
import org.threeten.bp.temporal.IsoFields

val LocalDate.weekOfWeekBasedYear: Int
    get() {
        return this.get(IsoFields.WEEK_OF_WEEK_BASED_YEAR)
    }
