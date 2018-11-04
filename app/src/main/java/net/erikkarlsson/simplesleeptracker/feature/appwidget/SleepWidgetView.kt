package net.erikkarlsson.simplesleeptracker.feature.appwidget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.os.Handler
import android.os.SystemClock
import android.util.DisplayMetrics
import android.view.WindowManager
import android.widget.RemoteViews
import net.erikkarlsson.simplesleeptracker.App
import net.erikkarlsson.simplesleeptracker.R
import net.erikkarlsson.simplesleeptracker.feature.appwidget.animation.CoinAnimation
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SleepWidgetView @Inject constructor(private val context: Context,
                                          private val sleepAppWidgetViewModel: SleepAppWidgetViewModel) {

    private val REFRESH_RATE = 100
    private var lastRedrawMillis: Long = 0
    lateinit var coinAnimation: CoinAnimation
    var density: Float = 0f
    var cwidth: Int = 0
    var cheight: Int = 0

    var isSleeping: Boolean? = null

    fun init() {
        sleepAppWidgetViewModel.state().observeForever({ render(it) })

        val wm = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val metrics = DisplayMetrics()
        wm.defaultDisplay.getMetrics(metrics)

        density = metrics.density
        cwidth = (72 * metrics.density).toInt()
        cheight = cwidth
    }

    private fun render(state: WidgetState?) {
        state?.let {
            if (state.isLoading) {
                return
            }

            if (isSleeping != state.isSleeping) {
                coinAnimation = CoinAnimation(density)
                scheduleRedraw()
            }

            isSleeping = state.isSleeping

            val views = RemoteViews(context.packageName, R.layout.app_widget)
//            val imageResId = if (state.isSleeping) R.drawable.owl_asleep else R.drawable.own_awake

//            views.setImageViewResource(R.id.button, imageResId)

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

    private fun redraw(context: Context) {
        val views = RemoteViews(context.packageName, R.layout.app_widget)
        val canvas = Bitmap.createBitmap(cwidth, cheight, Bitmap.Config.ARGB_8888)

        coinAnimation.draw(canvas)

        views.setImageViewBitmap(R.id.button, canvas)

        val appWidget = ComponentName(context, SleepAppWidgetProvider::class.java)
        val appWidgetManager = AppWidgetManager.getInstance(context)

        appWidgetManager.updateAppWidget(appWidget, views)

        if (!coinAnimation.animationFinished()) {
            scheduleRedraw()
        }
    }

    private fun scheduleRedraw() {
        var nextRedraw = lastRedrawMillis + REFRESH_RATE
        nextRedraw = if (nextRedraw > SystemClock.uptimeMillis())
            nextRedraw
        else
            SystemClock.uptimeMillis() + REFRESH_RATE
        scheduleRedrawAt(nextRedraw)
    }

    private fun scheduleRedrawAt(timeMillis: Long) {
        Handler().postAtTime({ redraw(App.getApplication()) }, timeMillis)
    }
}
