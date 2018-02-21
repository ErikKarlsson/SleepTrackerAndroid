package net.erikkarlsson.simplesleeptracker

import android.app.Activity
import android.app.Application
import android.content.BroadcastReceiver
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasActivityInjector
import dagger.android.HasBroadcastReceiverInjector
import net.erikkarlsson.simplesleeptracker.di.DaggerAppComponent
import net.erikkarlsson.simplesleeptracker.sleepappwidget.SleepWidgetView
import timber.log.Timber
import timber.log.Timber.DebugTree
import javax.inject.Inject


class App : Application(), HasActivityInjector, HasBroadcastReceiverInjector {

    @Inject
    lateinit var activityInjector : DispatchingAndroidInjector<Activity>

    @Inject
    lateinit var broadcastInjector : DispatchingAndroidInjector<BroadcastReceiver>

    @Inject
    lateinit var sleepWidgetView : SleepWidgetView

    override fun onCreate() {
        super.onCreate()

        DaggerAppComponent.builder().application(this).build().inject(this)

        Timber.plant(DebugTree())

        /*
        if (BuildConfig.DEBUG) {
            Timber.plant(DebugTree())
        } else {
            Timber.plant(CrashReportingTree())
        }
        */
    }

    override fun activityInjector(): AndroidInjector<Activity> = activityInjector

    override fun broadcastReceiverInjector(): AndroidInjector<BroadcastReceiver> = broadcastInjector

    /** A tree which logs important information for crash reporting.  */
    /*private class CrashReportingTree : Timber.Tree() {
        override fun log(priority: Int, tag: String, @NonNull message: String, t: Throwable?) {
            if (priority == Log.VERBOSE || priority == Log.DEBUG) {
                return
            }

            FakeCrashLibrary.log(priority, tag, message)

            if (t != null) {
                if (priority == Log.ERROR) {
                    FakeCrashLibrary.logError(t)
                } else if (priority == Log.WARN) {
                    FakeCrashLibrary.logWarning(t)
                }
            }
        }
    }
    */
}