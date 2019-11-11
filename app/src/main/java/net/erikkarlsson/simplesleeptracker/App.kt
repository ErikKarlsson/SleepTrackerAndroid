package net.erikkarlsson.simplesleeptracker

import android.annotation.SuppressLint
import android.app.Activity
import android.content.BroadcastReceiver
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import androidx.lifecycle.ProcessLifecycleOwner
import androidx.multidex.MultiDexApplication
import com.crashlytics.android.Crashlytics
import com.crashlytics.android.core.CrashlyticsCore
import com.jakewharton.threetenabp.AndroidThreeTen
import com.squareup.leakcanary.LeakCanary
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasActivityInjector
import dagger.android.HasBroadcastReceiverInjector
import dagger.android.support.HasSupportFragmentInjector
import io.fabric.sdk.android.Fabric
import net.erikkarlsson.simplesleeptracker.base.CrashReportingTree
import net.erikkarlsson.simplesleeptracker.di.AppComponent
import net.erikkarlsson.simplesleeptracker.di.DaggerAppComponent
import net.erikkarlsson.simplesleeptracker.features.appwidget.SleepAppWidgetController
import net.erikkarlsson.simplesleeptracker.features.appwidget.SleepWidgetView
import timber.log.Timber
import javax.inject.Inject

open class App : MultiDexApplication(), HasActivityInjector, HasSupportFragmentInjector,
        HasBroadcastReceiverInjector, LifecycleObserver {

    // TODO (erikkarlsson): Only needed for injecting Worker, remove once Dagger has released WorkerInjector.
    lateinit var appComponent: AppComponent

    @Inject
    lateinit var activityInjector: DispatchingAndroidInjector<Activity>

    @Inject
    lateinit var fragmentInjector: DispatchingAndroidInjector<Fragment>

    @Inject
    lateinit var broadcastInjector: DispatchingAndroidInjector<BroadcastReceiver>

    @Inject
    lateinit var sleepWidgetView: SleepWidgetView

    @Inject
    lateinit var sleepAppWidgetController: SleepAppWidgetController

    @SuppressLint("CheckResult")
    override fun onCreate() {
        super.onCreate()

        if (LeakCanary.isInAnalyzerProcess(this)) {
            return
        }

        ProcessLifecycleOwner.get().lifecycle.addObserver(this)

        LeakCanary.install(this)

        val crashlytics = Crashlytics.Builder()
                .core(CrashlyticsCore.Builder().disabled(BuildConfig.DEBUG).build())
                .build()

        Fabric.with(this, crashlytics)

        AndroidThreeTen.init(this)

        appComponent = DaggerAppComponent.builder().application(this).build()
        appComponent.inject(this)

        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        } else {
            Timber.plant(CrashReportingTree())
        }

        sleepAppWidgetController.updateWidget()
    }

    override fun activityInjector(): AndroidInjector<Activity> = activityInjector

    override fun supportFragmentInjector(): AndroidInjector<Fragment> = fragmentInjector

    override fun broadcastReceiverInjector(): AndroidInjector<BroadcastReceiver> = broadcastInjector

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
