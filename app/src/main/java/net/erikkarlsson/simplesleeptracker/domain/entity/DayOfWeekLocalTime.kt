package net.erikkarlsson.simplesleeptracker.domain.entity

import org.threeten.bp.DayOfWeek
import org.threeten.bp.LocalTime

data class DayOfWeekLocalTime(val dayOfWeek: DayOfWeek, val localTime: LocalTime)