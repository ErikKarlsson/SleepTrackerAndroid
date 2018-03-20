package net.erikkarlsson.simplesleeptracker.domain

import org.threeten.bp.LocalDate

// Replace with Java8 Period
data class DateRange(val from: LocalDate, val to: LocalDate)