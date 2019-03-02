package net.erikkarlsson.simplesleeptracker.domain

import android.app.Notification

interface Notifications {
    fun sendMinimumSleepNotification()
    fun getSleepDetectionNotification(): Notification
}
