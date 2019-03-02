package net.erikkarlsson.simplesleeptracker.data

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import net.erikkarlsson.simplesleeptracker.R
import net.erikkarlsson.simplesleeptracker.domain.Notifications
import javax.inject.Inject

class SystemNotifications @Inject constructor(private val context: Context) : Notifications {

    override fun sendMinimumSleepNotification() {
        val builder = NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_sleep_notification)
                .setContentTitle(context.getText(R.string.sleep_minimum_notification_title))
                .setContentText(context.getText(R.string.sleep_minimum_notification_description))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)

        with(NotificationManagerCompat.from(context)) {
            notify(MINIMUM_SLEEP_NOTIFICATION_ID, builder.build())
        }
    }

    override fun getSleepDetectionNotification(): Notification =
            NotificationCompat.Builder(context, FOREGROUND_NOTIFICATION_CHANNEL_ID)
                    .setSmallIcon(R.drawable.owl_asleep)
                    .setContentTitle(context.getText(R.string.sleep_detection_notification_title))
                    .setContentText(context.getText(R.string.sleep_detection_notification_description))
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                    .setOngoing(true)
                    .build()

    companion object {
        const val MINIMUM_SLEEP_NOTIFICATION_ID = 1
        const val NOTIFICATION_CHANNEL_ID = "net.erikkarlsson.simplesleeptracker.NOTIFICATION_CHANNEL_ID"
        const val FOREGROUND_NOTIFICATION_CHANNEL_ID = "net.erikkarlsson.simplesleeptracker.FOREGROUND_NOTIFICATION_CHANNEL_ID"

        fun createNotificationChannels(context: Context) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val notificationChannel = NotificationChannel(NOTIFICATION_CHANNEL_ID,
                        context.getString(R.string.notification_channel_name),
                        NotificationManager.IMPORTANCE_DEFAULT).apply {
                    description = context.getString(R.string.notification_channel_description)
                }

                val foregroundNotificationChannel = NotificationChannel(FOREGROUND_NOTIFICATION_CHANNEL_ID,
                        context.getString(R.string.foreground_notification_channel_name),
                        NotificationManager.IMPORTANCE_LOW).apply {
                    description = context.getString(R.string.foreground_notification_channel_description)
                }

                val notificationManager: NotificationManager =
                        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

                notificationManager.createNotificationChannel(notificationChannel)
                notificationManager.createNotificationChannel(foregroundNotificationChannel)
            }
        }
    }
}
