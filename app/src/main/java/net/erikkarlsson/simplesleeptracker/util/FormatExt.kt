package net.erikkarlsson.simplesleeptracker.util

import net.erikkarlsson.simplesleeptracker.domain.DayOfWeekLocalTime
import org.threeten.bp.LocalTime
import org.threeten.bp.OffsetDateTime
import org.threeten.bp.format.DateTimeFormatter
import org.threeten.bp.format.TextStyle
import java.util.*

val OffsetDateTime.formatYYYYMMDD: String get() = this.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
val OffsetDateTime.formatYYYYMMDDHHMM: String get() = this.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))

val DayOfWeekLocalTime.formatDisplayName: String
    get() = String.format("%s: %s",
            this.dayOfWeek.getDisplayName(TextStyle.FULL,
                    Locale.getDefault()).capitalize(),
            this.localTime.formatHHMM)


val LocalTime.formatHHMM: String get() = this.format(DateTimeFormatter.ofPattern("HH:mm"))

val Int.formatPercentage: String  get() {
    val prefix = if (this > 0) "+" else if (this < 0) "-" else ""
    return String.format("%s%d%%", prefix, this)
}

val Float.formatHHMM: String
    get() {
        val hours = Math.floor(this.toDouble()).toInt()
        val minutes = Math.floor(((this - hours) * 60).toDouble()).toInt()
        return String.format("%dh %dmin", hours, minutes)
    }