package net.erikkarlsson.simplesleeptracker.sleepappwidget

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import dagger.android.AndroidInjection
import io.reactivex.Observable
import net.erikkarlsson.simplesleeptracker.sleepappwidget.WidgetConstants.Companion.ACTION_SIMPLEAPPWIDGET
import javax.inject.Inject

class SleepAppWidgetProvider : AppWidgetProvider() {

    @Inject
    lateinit var sleepAppWidgetViewModel: SleepAppWidgetViewModel

    @Inject
    lateinit var context: Context

    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        AndroidInjection.inject(this, context)
        sleepAppWidgetViewModel.processIntents(Observable.just(WidgetIntent.InitialIntent))
    }

    override fun onReceive(context: Context, intent: Intent) {
        AndroidInjection.inject(this, context)
        super.onReceive(context, intent)

        if (ACTION_SIMPLEAPPWIDGET == intent.action) {
            sleepAppWidgetViewModel.processIntents(Observable.just(WidgetIntent.ToggleSleepIntent))
        }
    }
}