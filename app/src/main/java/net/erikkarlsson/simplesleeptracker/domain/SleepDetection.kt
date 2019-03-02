package net.erikkarlsson.simplesleeptracker.domain

interface SleepDetection {
    fun update()
    fun onStartDetectionReceived()
    fun onStopDetectionReceived()
}
