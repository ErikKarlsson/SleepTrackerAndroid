package net.erikkarlsson.simplesleeptracker

import android.app.Activity
import android.content.BroadcastReceiver
import android.support.multidex.MultiDexApplication
import com.crashlytics.android.Crashlytics
import com.crashlytics.android.core.CrashlyticsCore
import com.squareup.leakcanary.LeakCanary
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasActivityInjector
import dagger.android.HasBroadcastReceiverInjector
import io.fabric.sdk.android.Fabric
import net.erikkarlsson.simplesleeptracker.appwidget.SleepWidgetView
import net.erikkarlsson.simplesleeptracker.base.CrashReportingTree
import net.erikkarlsson.simplesleeptracker.di.DaggerAppComponent
import net.erikkarlsson.simplesleeptracker.elm.LogLevel
import net.erikkarlsson.simplesleeptracker.elm.RuntimeFactory
import timber.log.Timber
import javax.inject.Inject

open class App : MultiDexApplication(), HasActivityInjector, HasBroadcastReceiverInjector {

    @Inject
    lateinit var activityInjector: DispatchingAndroidInjector<Activity>

    @Inject
    lateinit var broadcastInjector: DispatchingAndroidInjector<BroadcastReceiver>

    @Inject
    lateinit var sleepWidgetView: SleepWidgetView

    override fun onCreate() {
        super.onCreate()

        if (LeakCanary.isInAnalyzerProcess(this)) {
            return
        }
        LeakCanary.install(this)

        val core = CrashlyticsCore.Builder().disabled(BuildConfig.DEBUG).build()
        Fabric.with(this, Crashlytics.Builder().core(core).build())

        DaggerAppComponent.builder().application(this).build().inject(this)

        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        } else {
            Timber.plant(CrashReportingTree())
        }

        RuntimeFactory.defaultLogLevel = if (BuildConfig.DEBUG) LogLevel.FULL else LogLevel.NONE
    }

    override fun activityInjector(): AndroidInjector<Activity> = activityInjector

    override fun broadcastReceiverInjector(): AndroidInjector<BroadcastReceiver> = broadcastInjector
}