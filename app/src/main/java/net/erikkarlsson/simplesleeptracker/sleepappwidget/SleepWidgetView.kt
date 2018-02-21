package net.erikkarlsson.simplesleeptracker.sleepappwidget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import net.erikkarlsson.simplesleeptracker.R
import net.erikkarlsson.simplesleeptracker.data.Sleep
import net.erikkarlsson.simplesleeptracker.sleepappwidget.WidgetConstants.Companion.ACTION_SIMPLEAPPWIDGET
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SleepWidgetView @Inject constructor(private val context: Context,
                                          sleepAppWidgetViewModel: SleepAppWidgetViewModel) {
    init {
        sleepAppWidgetViewModel.states()
            .subscribe(this::render,
                    { Timber.e(it, "Error getting states") })
    }

    private fun render(state: WidgetViewState) {
        Timber.d("render")
        val sleep = state.sleep
        val views = RemoteViews(context.packageName, R.layout.example_appwidget)
        val owlAsleep = sleep != Sleep.empty() && sleep.toDate == null
        val imageResId = if (owlAsleep) R.drawable.owl_asleep else R.drawable.own_awake

        views.setImageViewResource(R.id.button, imageResId)

        val intent = Intent(context, SleepAppWidgetProvider::class.java)
        intent.action = ACTION_SIMPLEAPPWIDGET

        val pendingIntent = PendingIntent.getBroadcast(context, 0, intent,
                PendingIntent.FLAG_UPDATE_CURRENT)

        views.setOnClickPendingIntent(R.id.button, pendingIntent)
        val appWidget = ComponentName(context, SleepAppWidgetProvider::class.java)
        val appWidgetManager = AppWidgetManager.getInstance(context)
        appWidgetManager.updateAppWidget(appWidget, views)
    }
}