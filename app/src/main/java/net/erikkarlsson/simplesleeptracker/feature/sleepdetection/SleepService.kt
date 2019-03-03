package net.erikkarlsson.simplesleeptracker.feature.sleepdetection

import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.IBinder
import dagger.android.AndroidInjection
import net.erikkarlsson.simplesleeptracker.domain.Notifications
import net.erikkarlsson.simplesleeptracker.domain.entity.ActionType
import timber.log.Timber
import javax.inject.Inject

class SleepService : Service() {

    private var receiver: BroadcastReceiver? = null

    @Inject
    lateinit var notifications: Notifications

    @Inject
    lateinit var controller: SleepServiceController

    override fun onCreate() {
        Timber.d("SleepService onCreate")
        AndroidInjection.inject(this)
        super.onCreate()
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        Timber.d("SleepService onStartCommand")

        when (intent.action) {
            STOP_FOREGROUND_ACTION -> {
                stopForeground(true)
                stopSelf()
            }
            else -> {
                showNotification()
                registerScreenReceiver()
            }
        }

        return Service.START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        controller.onDestroy()
        receiver?.let { unregisterReceiver(it) }
        receiver = null
    }

    private fun registerScreenReceiver() {
        val filter = IntentFilter(Intent.ACTION_SCREEN_ON)
        filter.addAction(Intent.ACTION_SCREEN_OFF)
        receiver = ScreenReceiver()
        registerReceiver(receiver, filter)
    }

    private fun showNotification() {
        val notification = notifications.getSleepDetectionNotification()
        startForeground(101, notification)
    }

    inner class ScreenReceiver : BroadcastReceiver() {

        override fun onReceive(context: Context, intent: Intent) {
            if (intent.action == Intent.ACTION_SCREEN_OFF) {
                Timber.d("ACTION_SCREEN_OFF")
                controller.onAction(ActionType.SCREEN_OFF)
            } else if (intent.action == Intent.ACTION_SCREEN_ON) {
                Timber.d("ACTION_SCREEN_ON")
                controller.onAction(ActionType.SCREEN_ON)
            }
        }

    }

    companion object {
        const val START_FOREGROUND_ACTION = "net.erikkarlsson.simplesleeptracker.START_FOREGROUND_ACTION"
        const val STOP_FOREGROUND_ACTION = "net.erikkarlsson.simplesleeptracker.STOP_FOREGROUND_ACTION"
    }

}
