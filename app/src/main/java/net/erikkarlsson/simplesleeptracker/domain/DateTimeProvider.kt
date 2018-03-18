package net.erikkarlsson.simplesleeptracker.domain

import org.threeten.bp.OffsetDateTime

interface DateTimeProvider {
    fun now(): OffsetDateTime
}