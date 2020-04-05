package net.erikkarlsson.simplesleeptracker.domain

interface AppLifecycle {
    fun isForegrounded(): Boolean
}
