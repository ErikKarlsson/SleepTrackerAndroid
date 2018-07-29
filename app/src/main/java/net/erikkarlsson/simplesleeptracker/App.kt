package net.erikkarlsson.simplesleeptracker

import android.app.Activity
import android.content.BroadcastReceiver
import android.support.multidex.MultiDexApplication
import android.support.v4.app.Fragment
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
import net.erikkarlsson.simplesleeptracker.elm.LogLevel
import net.erikkarlsson.simplesleeptracker.elm.RuntimeFactory
import net.erikkarlsson.simplesleeptracker.feature.appwidget.SleepWidgetView
import net.erikkarlsson.simplesleeptracker.feature.backup.PeriodicBackupScheduler
import timber.log.Timber
import javax.inject.Inject

open class App : MultiDexApplication(), HasActivityInjector, HasSupportFragmentInjector, HasBroadcastReceiverInjector {

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
    lateinit var periodicBackupScheduler: PeriodicBackupScheduler

    override fun onCreate() {
        super.onCreate()

        if (LeakCanary.isInAnalyzerProcess(this)) {
            return
        }
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

        RuntimeFactory.defaultLogLevel = if (BuildConfig.DEBUG) LogLevel.FULL else LogLevel.NONE
    }

    override fun activityInjector(): AndroidInjector<Activity> = activityInjector

    override fun supportFragmentInjector(): AndroidInjector<Fragment> = fragmentInjector

    override fun broadcastReceiverInjector(): AndroidInjector<BroadcastReceiver> = broadcastInjector
}