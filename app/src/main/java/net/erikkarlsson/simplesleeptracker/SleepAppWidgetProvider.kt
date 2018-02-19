package net.erikkarlsson.simplesleeptracker

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import dagger.android.AndroidInjection
import io.reactivex.Completable
import io.reactivex.Maybe
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import net.erikkarlsson.simplesleeptracker.data.Sleep
import net.erikkarlsson.simplesleeptracker.data.SleepDao
import net.erikkarlsson.simplesleeptracker.data.SleepDatabase
import net.erikkarlsson.simplesleeptracker.data.SleepRepository
import org.threeten.bp.OffsetDateTime
import org.threeten.bp.temporal.ChronoUnit
import timber.log.Timber
import java.math.BigDecimal
import javax.inject.Inject

class SleepAppWidgetProvider : AppWidgetProvider() {

    lateinit var database: SleepDatabase
    lateinit var sleepDao: SleepDao

    @Inject
    lateinit var sleepRepository: SleepRepository

    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        initDependencies(context)
        Timber.d("onUpdate")
        print("ExampleAppWidgetProvider onUpdate")
        // There may be multiple widgets active, so update all of them
        for (appWidgetId in appWidgetIds) {
            setAppWidgetClickListener(context, appWidgetManager, appWidgetId)
        }

        sleepDao.getCurrentSleep()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ renderSleep(context, it) },
                        { Timber.d("Error getting sleep: %s", it.message) },
                        { renderSleep(context, null) })
    }

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)
        AndroidInjection.inject(this, context);
        initDependencies(context)
        if (ACTION_SIMPLEAPPWIDGET == intent.action) {
            toggleSleep(context)
        }
    }

    private fun toggleSleep(context: Context) {
        sleepDao.getCurrentSleep()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .flatMap { updateDb(it) }
                .subscribe({ renderSleep(context, it) },
                        { Timber.d("Error toggling sleep: %s", it.message) },
                        { onNoSleepFound(context) })
    }

    private fun onNoSleepFound(context: Context) {
        insertNewSleepInDb()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ renderSleep(context, it) },
                        { Timber.d("Error inserting sleep: %s", it.message) },
                        {})
    }

    private fun updateDb(currentSleep: Sleep): Maybe<Sleep> {
        if (currentSleep.toDate == null) {
            return updateSleepInDb(currentSleep)
        } else {
            return insertNewSleepInDb()
        }
    }

    private fun insertNewSleepInDb(): Maybe<Sleep> {
        val newSleep = Sleep(OffsetDateTime.now())
        return Completable.fromAction({ sleepDao.insertSleep(newSleep) })
                .andThen(sleepDao.getCurrentSleep())
                .subscribeOn(Schedulers.io())
    }

    private fun updateSleepInDb(currentSleep: Sleep): Maybe<Sleep> {
        val fromDate = currentSleep.fromDate

        if (fromDate == null) {
            throw RuntimeException("currentSleep has invalid state, fromDate is null")
        }

        val toDate = OffsetDateTime.now()
        val hours = calculateHoursBetweenDates(fromDate, toDate)
        val fromMidnightOffset = calculateOffsetFromMidnightInMinutes(fromDate)
        val toMidnightOffset = calculateOffsetFromMidnightInMinutes(toDate)
        val updatedSleep = currentSleep.copy(toDate = toDate, hours = hours)

        return Completable.fromAction({ sleepDao.updateSleep(updatedSleep) })
                .andThen(Maybe.just(updatedSleep))
                .subscribeOn(Schedulers.io())
    }

    /**
     * Calculates offset from midnight in minutes.
     * For time between 00:00 - 12:00 the offset will be positive. eg. 60 minutes for 01:00
     * For time between 12:00 - 00:00 the offset will be negative. eg. -60 minutes for 23:00
     * The offset can be used to calculate average time.
     */
    private fun calculateOffsetFromMidnightInMinutes(date: OffsetDateTime): Int {
        val hour = date.hour
        val minutes = date.hour * 60 + date.minute

        if (hour <= 12) {
            return minutes
        } else {
            return minutes - 3600
        }
    }

    private fun calculateHoursBetweenDates(fromDate: OffsetDateTime, toDate: OffsetDateTime): Float {
        val secondsBetweenDates = ChronoUnit.SECONDS.between(fromDate, toDate)
        return BigDecimal.valueOf(secondsBetweenDates / 3600.0)
                .setScale(3, BigDecimal.ROUND_HALF_UP)
                .toFloat()
    }

    private fun renderSleep(context: Context,
                            sleep: Sleep?) {

        sleepDao.getAverageSleepInHours()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ Timber.d("Average sleep: %f", it) },
                        {})

        sleepDao.getSleep()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ Timber.d("Sleep size: %d", it.size) },
                        {})

        sleepDao.getLongestSleep()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    Timber.d("Sleep wakeup time: %f", it)
                },
                        {})

        // Construct the RemoteViews object
        val views = RemoteViews(context.packageName,
                R.layout.example_appwidget)

        val owlAsleep = sleep != null && sleep.toDate == null

        val imageResId = if (owlAsleep) R.drawable.owl_asleep else R.drawable.own_awake

        views.setImageViewResource(R.id.button, imageResId)

        // This time we dont have widgetId. Reaching our widget with that way.
        val appWidget = ComponentName(context, AppWidgetProvider::class.java)
        val appWidgetManager = AppWidgetManager.getInstance(context)
        // Instruct the widget manager toDate update the widget
        appWidgetManager.updateAppWidget(appWidget, views)
    }

    private fun initDependencies(context: Context) {
        database = SleepDatabase.getInstance(context)
        sleepDao = database.sleepDao()
    }

    companion object {

        private val ACTION_SIMPLEAPPWIDGET = "ACTION_BROADCASTWIDGETSAMPLE_TEST"

        internal fun setAppWidgetClickListener(context: Context,
                                               appWidgetManager: AppWidgetManager,
                                               appWidgetId: Int) {
            // Construct the RemoteViews object
            val views = RemoteViews(context.packageName, R.layout.example_appwidget)
            // Construct an Intent which is pointing this class.
            val intent = Intent(context, AppWidgetProvider::class.java)
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