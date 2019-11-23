package net.erikkarlsson.simplesleeptracker.core.util

import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.RelativeSizeSpan
import net.easypark.dateutil.MINUTES_IN_AN_HOUR
import org.threeten.bp.LocalDate
import org.threeten.bp.OffsetDateTime
import org.threeten.bp.format.DateTimeFormatter
import java.text.SimpleDateFormat
import java.util.*

val OffsetDateTime.formatHHMM: String get() = this.format(DateTimeFormatter.ofPattern("HH:mm"))
val OffsetDateTime.formatDateDisplayName: String get() = this.format(DateTimeFormatter.ofPattern("EEE d MMM"))
val OffsetDateTime.formatDateDisplayName2: String get() = this.format(DateTimeFormatter.ofPattern("EEEE d MMMM"))
val LocalDate.formatDateDisplayName2: String get() = this.format(DateTimeFormatter.ofPattern("EEEE d MMMM"))

val LocalDate.yearLastTwoDigits: String
    get() {
        return this.format(DateTimeFormatter.ofPattern("yy"))
    }

val LocalDate.formatDateShort: String
    get() {
        return this.format(DateTimeFormatter.ofPattern("d MMM yyyy"))
    }

val Int.formatPercentage: String
    get() {
        val prefix = if (this > 0) "+" else if (this < 0) "-" else ""
        return String.format("%s%d%%", prefix, this)
    }


val Float.formatHoursMinutesSpannable: Spannable
    get() {
        val totalMinutes = Math.round((Math.abs(this) * MINUTES_IN_AN_HOUR).toDouble()).toInt()
        val hours = Math.floor((totalMinutes / MINUTES_IN_AN_HOUR).toDouble()).toInt()
        val minutes = if (hours > 0) {
            totalMinutes % (hours * MINUTES_IN_AN_HOUR)
        } else {
            totalMinutes
        }

        return spannableFrom(hours, minutes)
    }

private fun spannableFrom(hours: Int, minutes: Int): Spannable =
        when {
            hours == 0 && minutes > 0 -> {
                val string = String.format("%dmin", minutes)
                val spannableStringBuilder = SpannableStringBuilder(string)
                spannableStringBuilder.setSpan(
                        RelativeSizeSpan(0.7f),
                        string.length - 3,
                        string.length,
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                )
                spannableStringBuilder

            }
            hours > 0 && minutes == 0 -> {
                val string = String.format("%dh", hours)
                val spannableStringBuilder = SpannableStringBuilder(string)
                spannableStringBuilder.setSpan(
                        RelativeSizeSpan(0.7f),
                        hours.toString().length,
                        hours.toString().length + 1,
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                )
                spannableStringBuilder
            }
            hours > 0 && minutes > 0 -> {
                val string = String.format("%dh %dmin", hours, minutes)
                val spannableStringBuilder = SpannableStringBuilder(string)
                spannableStringBuilder.setSpan(
                        RelativeSizeSpan(0.7f),
                        hours.toString().length,
                        hours.toString().length + 1,
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                )
                spannableStringBuilder.setSpan(
                        RelativeSizeSpan(0.7f),
                        string.length - 3,
                        string.length,
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                )
                spannableStringBuilder
            }
            else -> {
                val string = "0h"
                val spannableStringBuilder = SpannableStringBuilder(string)
                spannableStringBuilder.setSpan(
                        RelativeSizeSpan(0.7f),
                        1,
                        2,
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                )
                spannableStringBuilder
            }
        }

val Float.formatHoursMinutes2: String
    get() {
        val totalMinutes = Math.round((Math.abs(this) * MINUTES_IN_AN_HOUR).toDouble()).toInt()
        val hours = Math.floor((totalMinutes / MINUTES_IN_AN_HOUR).toDouble()).toInt()
        val minutes = if (hours > 0) {
            totalMinutes % (hours * MINUTES_IN_AN_HOUR)
        } else {
            totalMinutes
        }

        return when {
            hours == 0 -> String.format("%dmin", minutes)
            else -> String.format("%d h %d min", hours, minutes)
        }
    }

val Float.formatHoursMinutes3: String
    get() {
        val totalMinutes = Math.round((Math.abs(this) * MINUTES_IN_AN_HOUR).toDouble()).toInt()
        val hours = Math.floor((totalMinutes / MINUTES_IN_AN_HOUR).toDouble()).toInt()
        val minutes = if (hours > 0) {
            totalMinutes % (hours * MINUTES_IN_AN_HOUR)
        } else {
            totalMinutes
        }

        return when {
            hours == 0 && minutes > 0 -> String.format("0.%02d", minutes)
            hours > 0 && minutes == 0 -> String.format("%d", hours)
            hours > 0 && minutes > 0 -> String.format("%d.%02d", hours, minutes)
            else -> "0"
        }
    }

val Float.formatHoursMinutes: String
    get() {
        val totalMinutes = Math.round((Math.abs(this) * MINUTES_IN_AN_HOUR).toDouble()).toInt()
        val hours = Math.floor((totalMinutes / MINUTES_IN_AN_HOUR).toDouble()).toInt()
        val minutes = if (hours > 0) {
            totalMinutes % (hours * MINUTES_IN_AN_HOUR)
        } else {
            totalMinutes
        }

        return when {
            hours == 0 && minutes > 0 -> String.format("%dmin", minutes)
            hours > 0 && minutes == 0 -> String.format("%dh", hours)
            hours > 0 && minutes > 0 -> String.format("%dh %dmin", hours, minutes)
            else -> "0h"
        }
    }

val Float.formatHoursMinutesWithPrefix: String
    get() {
        val prefix = if (this > 0) "+" else "-"
        return prefix + this.formatHoursMinutes
    }

val Long.formatTimestamp: String
    get() {
        val date = Date(this)
        val format = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
        return format.format(date)
    }
