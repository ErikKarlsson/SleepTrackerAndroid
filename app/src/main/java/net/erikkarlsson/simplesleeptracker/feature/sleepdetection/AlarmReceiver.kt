package net.erikkarlsson.simplesleeptracker.feature.sleepdetection

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import dagger.android.AndroidInjection
import net.erikkarlsson.simplesleeptracker.data.sleepdetection.SleepDetectionServiceScheduler.Companion.START_SLEEP_DETECTION_ACTION
import net.erikkarlsson.simplesleeptracker.data.sleepdetection.SleepDetectionServiceScheduler.Companion.STOP_SLEEP_DETECTION_ACTION
import net.erikkarlsson.simplesleeptracker.domain.SleepDetectionScheduler
import javax.inject.Inject

/**
 * Broadcast receiver for the alarm, which delivers the notification.
 */
class AlarmReceiver : BroadcastReceiver() {

    @Inject
    lateinit var sleepDetectionScheduler: SleepDetectionScheduler

    override fun onReceive(context: Context, intent: Intent) {
        AndroidInjection.inject(this, context)

        when (intent.action) {
            START_SLEEP_DETECTION_ACTION -> {
                sleepDetectionScheduler.startDetection()
            }
            STOP_SLEEP_DETECTION_ACTION -> {
                sleepDetectionScheduler.stopDetection()
            }
            else -> throw IllegalArgumentException("action not provided")
        }
    }

}
