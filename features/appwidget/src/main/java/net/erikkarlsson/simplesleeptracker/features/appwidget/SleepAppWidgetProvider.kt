package net.erikkarlsson.simplesleeptracker.features.appwidget

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import dagger.android.AndroidInjection
import javax.inject.Inject

class SleepAppWidgetProvider : AppWidgetProvider() {

    @Inject
    lateinit var sleepAppWidgetController: SleepAppWidgetController

    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        AndroidInjection.inject(this, context)
        sleepAppWidgetController.initialize()
    }

    override fun onReceive(context: Context, intent: Intent) {
        AndroidInjection.inject(this, context)
        super.onReceive(context, intent)

        if (ACTION_SIMPLEAPPWIDGET_TOGGLE == intent.action) {
            sleepAppWidgetController.onToggleSleepClick()
        }
    }
}
