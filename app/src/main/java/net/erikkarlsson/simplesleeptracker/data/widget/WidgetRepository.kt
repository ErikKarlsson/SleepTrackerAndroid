package net.erikkarlsson.simplesleeptracker.data.widget

import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Context
import android.os.Build
import io.reactivex.Single
import net.erikkarlsson.simplesleeptracker.domain.WidgetDataSource
import net.erikkarlsson.simplesleeptracker.feature.appwidget.SleepAppWidgetProvider
import javax.inject.Inject

class WidgetRepository @Inject constructor(private val context: Context) : WidgetDataSource {
    override fun isPinningWidgetSupported(): Single<Boolean> =
        Single.just(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)

    override fun isWidgetAdded(): Single<Boolean> =
        Single.fromCallable {
            val ids = AppWidgetManager.getInstance(context)
                    .getAppWidgetIds(ComponentName(context, SleepAppWidgetProvider::class.java))
            ids.size > 0
        }
}
