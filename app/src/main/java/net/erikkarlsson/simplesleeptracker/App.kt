package net.erikkarlsson.simplesleeptracker

import androidx.hilt.work.HiltWorkerFactory
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import androidx.lifecycle.ProcessLifecycleOwner
import androidx.multidex.MultiDexApplication
import androidx.work.Configuration
import com.jakewharton.threetenabp.AndroidThreeTen
import dagger.hilt.android.HiltAndroidApp
import net.erikkarlsson.simplesleeptracker.features.appwidget.SleepAppWidgetController
import net.erikkarlsson.simplesleeptracker.features.appwidget.SleepWidgetView
import timber.log.Timber
import javax.inject.Inject

@HiltAndroidApp
open class App : MultiDexApplication(), Configuration.Provider, LifecycleObserver {

    @Inject
    lateinit var sleepWidgetView: SleepWidgetView

    @Inject
    lateinit var sleepAppWidgetController: SleepAppWidgetController

    @Inject
    lateinit var workerFactory: HiltWorkerFactory

    override fun onCreate() {
        super.onCreate()

        ProcessLifecycleOwner.get().lifecycle.addObserver(this)

        AndroidThreeTen.init(this)

        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        } else {
            Timber.plant(CrashReportingTree())
        }

        sleepAppWidgetController.initialize()
    }

    override fun getWorkManagerConfiguration() =
            Configuration.Builder()
                    .setWorkerFactory(workerFactory)
                    .build()

    // https://android.jlelse.eu/how-to-detect-android-application-open-and-close-background-and-foreground-events-1b4713784b57
    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    fun onAppBackgrounded() {
        isForegrounded = false
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    fun onAppForegrounded() {
        isForegrounded = true
    }

    companion object {
        var isForegrounded = false
    }
}
