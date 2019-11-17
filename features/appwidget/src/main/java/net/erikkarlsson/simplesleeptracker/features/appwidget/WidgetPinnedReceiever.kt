package net.erikkarlsson.simplesleeptracker.features.appwidget

import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import timber.log.Timber

class WidgetPinnedReceiever: BroadcastReceiver() {
    companion object {
        const val BROADCAST_ID = 123456

        fun getPendingIntent(context: Context): PendingIntent {
            val callbackIntent = Intent(context, WidgetPinnedReceiever::class.java)
            return PendingIntent.getBroadcast(
                    context, BROADCAST_ID, callbackIntent, PendingIntent.FLAG_UPDATE_CURRENT)
        }
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        Timber.d("Widget pinned")
    }
}
