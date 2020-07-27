package net.erikkarlsson.simplesleeptracker.features.appwidget.data

import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import net.erikkarlsson.simplesleeptracker.domain.WidgetDataSource
import net.erikkarlsson.simplesleeptracker.features.appwidget.SleepAppWidgetProvider
import javax.inject.Inject

class WidgetRepository @Inject constructor(@ApplicationContext private val context: Context) : WidgetDataSource {

    override fun isWidgetAdded(): Boolean {
            val ids = AppWidgetManager.getInstance(context)
                    .getAppWidgetIds(ComponentName(context, SleepAppWidgetProvider::class.java))
            return ids.size > 0
    }
}
