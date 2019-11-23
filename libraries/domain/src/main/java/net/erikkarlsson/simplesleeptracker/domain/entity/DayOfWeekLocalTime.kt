package net.erikkarlsson.simplesleeptracker.domain.entity

import com.google.common.collect.ImmutableList
import net.easypark.dateutil.formatHHMM
import org.threeten.bp.DayOfWeek
import org.threeten.bp.LocalTime
import org.threeten.bp.format.TextStyle
import java.util.*

data class DayOfWeekLocalTime(val dayOfWeek: DayOfWeek, val localTime: LocalTime)

val DayOfWeekLocalTime.formatDisplayName: String
    get() = String.format("%s: %s",
            this.dayOfWeek.getDisplayName(TextStyle.FULL, Locale.getDefault()).capitalize(),
            this.localTime.formatHHMM)

val ImmutableList<DayOfWeekLocalTime>.formatDisplayNameTime: String
    get() = this.map { " - " + it.formatDisplayName }.joinToString(separator = "\n")
