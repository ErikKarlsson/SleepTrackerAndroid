package net.erikkarlsson.simplesleeptracker.features.appwidget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SleepWidgetView @Inject constructor(@ApplicationContext private val context: Context) {

    fun render(isSleeping: Boolean) {
        val views = RemoteViews(context.packageName, R.layout.app_widget)
        val imageResId = if (isSleeping) R.drawable.owl_asleep else R.drawable.own_awake

        views.setImageViewResource(R.id.button, imageResId)

        val intent = Intent(context, SleepAppWidgetProvider::class.java)
        intent.action = ACTION_SIMPLEAPPWIDGET_TOGGLE

        val pendingIntent = PendingIntent.getBroadcast(context, 0, intent,
                PendingIntent.FLAG_UPDATE_CURRENT)

        views.setOnClickPendingIntent(R.id.button, pendingIntent)

        val appWidget = ComponentName(context, SleepAppWidgetProvider::class.java)
        val appWidgetManager = AppWidgetManager.getInstance(context)

        appWidgetManager.updateAppWidget(appWidget, views)
    }
}
