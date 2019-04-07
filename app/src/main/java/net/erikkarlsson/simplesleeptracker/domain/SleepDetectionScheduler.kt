package net.erikkarlsson.simplesleeptracker.domain

interface SleepDetectionScheduler {
    fun schedule()
    fun startDetection()
    fun stopDetection()
}
