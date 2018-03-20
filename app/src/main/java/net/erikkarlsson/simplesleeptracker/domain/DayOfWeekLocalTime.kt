package net.erikkarlsson.simplesleeptracker.domain

import org.threeten.bp.DayOfWeek
import org.threeten.bp.LocalTime

data class DayOfWeekLocalTime(val dayOfWeek: DayOfWeek, val localTime: LocalTime)