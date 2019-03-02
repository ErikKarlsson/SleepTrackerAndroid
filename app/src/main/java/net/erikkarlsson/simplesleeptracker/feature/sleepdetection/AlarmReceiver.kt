package net.erikkarlsson.simplesleeptracker.feature.sleepdetection

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import dagger.android.AndroidInjection
import net.erikkarlsson.simplesleeptracker.data.SleepDetectionAlarm.Companion.START_SLEEP_DETECTION_ACTION
import net.erikkarlsson.simplesleeptracker.data.SleepDetectionAlarm.Companion.STOP_SLEEP_DETECTION_ACTION
import net.erikkarlsson.simplesleeptracker.domain.SleepDetection
import javax.inject.Inject

/**
 * Broadcast receiver for the alarm, which delivers the notification.
 */
class AlarmReceiver : BroadcastReceiver() {

    @Inject
    lateinit var sleepDetection: SleepDetection

    override fun onReceive(context: Context, intent: Intent) {
        AndroidInjection.inject(this, context)

        when (intent.action) {
            START_SLEEP_DETECTION_ACTION -> {
                sleepDetection.onStartDetectionReceived()
            }
            STOP_SLEEP_DETECTION_ACTION -> {
                sleepDetection.onStopDetectionReceived()
            }
            else -> throw IllegalArgumentException("action not provided")
        }
    }

}
