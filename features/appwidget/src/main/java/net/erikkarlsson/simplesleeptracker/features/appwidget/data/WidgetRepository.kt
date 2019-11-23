package net.erikkarlsson.simplesleeptracker.features.appwidget.data

import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Context
import io.reactivex.Single
import net.erikkarlsson.simplesleeptracker.domain.WidgetDataSource
import net.erikkarlsson.simplesleeptracker.features.appwidget.SleepAppWidgetProvider
import javax.inject.Inject

class WidgetRepository @Inject constructor(private val context: Context) : WidgetDataSource {

    override fun isWidgetAdded(): Single<Boolean> =
        Single.fromCallable {
            val ids = AppWidgetManager.getInstance(context)
                    .getAppWidgetIds(ComponentName(context, SleepAppWidgetProvider::class.java))
            ids.size > 0
        }
}
