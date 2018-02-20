package net.erikkarlsson.simplesleeptracker.sleepappwidget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import dagger.android.AndroidInjection
import net.erikkarlsson.simplesleeptracker.R
import net.erikkarlsson.simplesleeptracker.data.Sleep
import timber.log.Timber
import javax.inject.Inject

class SleepAppWidgetProvider : AppWidgetProvider() {

    @Inject
    lateinit var sleepAppWidgetController: SleepAppWidgetController

    @Inject
    lateinit var context: Context

    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        AndroidInjection.inject(this, context)
        Timber.d("onUpdate")
        print("ExampleAppWidgetProvider onUpdate")
        // There may be multiple widgets active, so update all of them
        for (appWidgetId in appWidgetIds) {
            setAppWidgetClickListener(context, appWidgetManager, appWidgetId)
        }

        sleepAppWidgetController.getSleepStream()
                .subscribe(this::renderSleep,
                        { Timber.e(it, "Sleep stream should never terminate") })
    }

    override fun onReceive(context: Context, intent: Intent) {
        AndroidInjection.inject(this, context)
        super.onReceive(context, intent)

        if (ACTION_SIMPLEAPPWIDGET == intent.action) {
            sleepAppWidgetController.toggleSleep()
        }
    }

    private fun renderSleep(sleep: Sleep) {
        // Construct the RemoteViews object
        val views = RemoteViews(context.packageName,
                R.layout.example_appwidget)

        val owlAsleep = sleep.toDate == null

        val imageResId = if (owlAsleep) R.drawable.owl_asleep else R.drawable.own_awake

        views.setImageViewResource(R.id.button, imageResId)

        // This time we dont have widgetId. Reaching our widget with that way.
        val appWidget = ComponentName(context, SleepAppWidgetProvider::class.java)
        val appWidgetManager = AppWidgetManager.getInstance(context)
        // Instruct the widget manager toDate update the widget
        appWidgetManager.updateAppWidget(appWidget, views)
    }

    companion object {

        private val ACTION_SIMPLEAPPWIDGET = "ACTION_BROADCASTWIDGETSAMPLE_TEST"

        internal fun setAppWidgetClickListener(context: Context,
                                               appWidgetManager: AppWidgetManager,
                                               appWidgetId: Int) {
            // Construct the RemoteViews object
            val views = RemoteViews(context.packageName, R.layout.example_appwidget)
            // Construct an Intent which is pointing this class.
            val intent = Intent(context, SleepAppWidgetProvider::class.java)
            intent.action = ACTION_SIMPLEAPPWIDGET
            // And this time we are sending a broadcast with getBroadcast
            val pendingIntent = PendingIntent.getBroadcast(context, 0, intent,
                    PendingIntent.FLAG_UPDATE_CURRENT)

            views.setOnClickPendingIntent(R.id.button, pendingIntent)
            // Instruct the widget manager toDate update the widget
            appWidgetManager.updateAppWidget(appWidgetId, views)
        }
    }
}