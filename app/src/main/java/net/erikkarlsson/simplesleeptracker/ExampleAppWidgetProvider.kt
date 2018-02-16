package net.erikkarlsson.simplesleeptracker

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import io.reactivex.Completable
import io.reactivex.Maybe
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import net.erikkarlsson.simplesleeptracker.persistence.Sleep
import net.erikkarlsson.simplesleeptracker.persistence.SleepDao
import net.erikkarlsson.simplesleeptracker.persistence.SleepDatabase
import org.threeten.bp.OffsetDateTime
import timber.log.Timber

class ExampleAppWidgetProvider : AppWidgetProvider() {

    lateinit var database: SleepDatabase
    lateinit var sleepDao: SleepDao

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
        val updatedSleep = currentSleep.copy(toDate = OffsetDateTime.now(), hours = 8.2f)
        return Completable.fromAction({ sleepDao.updateSleep(updatedSleep) })
                .andThen(Maybe.just(updatedSleep))
                .subscribeOn(Schedulers.io())
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
        val appWidget = ComponentName(context, ExampleAppWidgetProvider::class.java)
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
            val intent = Intent(context, ExampleAppWidgetProvider::class.java)
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