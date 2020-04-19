package net.erikkarlsson.simplesleeptracker

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import androidx.lifecycle.ProcessLifecycleOwner
import androidx.multidex.MultiDexApplication
import androidx.work.Configuration
import androidx.work.WorkManager
import com.jakewharton.threetenabp.AndroidThreeTen
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasAndroidInjector
import net.erikkarlsson.simplesleeptracker.di.DaggerAppComponent
import net.erikkarlsson.simplesleeptracker.features.appwidget.SleepAppWidgetController
import net.erikkarlsson.simplesleeptracker.features.appwidget.SleepWidgetView
import net.erikkarlsson.simplesleeptracker.features.backup.di.MyWorkerFactory
import timber.log.Timber
import javax.inject.Inject

open class App : MultiDexApplication(), HasAndroidInjector, LifecycleObserver {

    @Inject
    lateinit var androidInjector: DispatchingAndroidInjector<Any>

    @Inject
    lateinit var sleepWidgetView: SleepWidgetView

    @Inject
    lateinit var sleepAppWidgetController: SleepAppWidgetController

    @Inject
    lateinit var myWorkerFactory: MyWorkerFactory

    override fun onCreate() {
        super.onCreate()

        ProcessLifecycleOwner.get().lifecycle.addObserver(this)

        AndroidThreeTen.init(this)

        DaggerAppComponent.builder().application(this).build().inject(this)

        WorkManager.initialize(this, Configuration.Builder().setWorkerFactory(myWorkerFactory).build())

        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        } else {
            Timber.plant(CrashReportingTree())
        }

        sleepAppWidgetController.initialize()
    }

    override fun androidInjector(): AndroidInjector<Any> = androidInjector

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
