package net.erikkarlsson.simplesleeptracker.data

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import dagger.hilt.android.qualifiers.ApplicationContext
import net.erikkarlsson.simplesleeptracker.domain.Notifications
import javax.inject.Inject

private const val MINIMUM_SLEEP_NOTIFICATION_ID = 1
private const val CHANNEL_ID = "sleep_channel_id"

class SystemNotifications @Inject constructor(@ApplicationContext private val context: Context): Notifications {
    override fun sendMinimumSleepNotification() {
        createNotificationChannel()

        val contentTitle = context.getText(R.string.sleep_minimum_notification_title)
        val contentDescription = context.getText(R.string.sleep_minimum_notification_description)

        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_sleep_notification)
                .setContentTitle(contentTitle)
                .setContentText(contentDescription)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)

        with(NotificationManagerCompat.from(context)) {
            notify(MINIMUM_SLEEP_NOTIFICATION_ID, builder.build())
        }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = context.getString(R.string.notification_channel_name)
            val descriptionText = context.getString(R.string.notification_channel_description)
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            val notificationManager: NotificationManager =
                        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
}
