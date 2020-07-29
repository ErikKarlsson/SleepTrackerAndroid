package net.erikkarlsson.simplesleeptracker.features.appwidget

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class SleepAppWidgetProvider : AppWidgetProvider() {

    @Inject
    lateinit var sleepAppWidgetController: SleepAppWidgetController

    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        sleepAppWidgetController.initialize()
    }

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)

        if (ACTION_SIMPLEAPPWIDGET_TOGGLE == intent.action) {
            sleepAppWidgetController.onToggleSleepClick()
        }
    }
}
