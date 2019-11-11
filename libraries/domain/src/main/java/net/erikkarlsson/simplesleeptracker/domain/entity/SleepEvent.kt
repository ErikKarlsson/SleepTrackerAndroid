package net.erikkarlsson.simplesleeptracker.domain.entity

sealed class SleepEvent // One time event
object MinimumSleepEvent : SleepEvent()
