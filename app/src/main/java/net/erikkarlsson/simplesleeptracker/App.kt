package net.erikkarlsson.simplesleeptracker

import android.app.Activity
import android.content.BroadcastReceiver
import android.support.multidex.MultiDexApplication
import com.google.firebase.crash.FirebaseCrash
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasActivityInjector
import dagger.android.HasBroadcastReceiverInjector
import net.erikkarlsson.simplesleeptracker.appwidget.SleepWidgetView
import net.erikkarlsson.simplesleeptracker.base.FirebaseTree
import net.erikkarlsson.simplesleeptracker.di.DaggerAppComponent
import net.erikkarlsson.simplesleeptracker.elm.LogLevel
import net.erikkarlsson.simplesleeptracker.elm.RuntimeFactory
import timber.log.Timber
import timber.log.Timber.DebugTree
import javax.inject.Inject


open class App : MultiDexApplication(), HasActivityInjector, HasBroadcastReceiverInjector {

    @Inject
    lateinit var activityInjector : DispatchingAndroidInjector<Activity>

    @Inject
    lateinit var broadcastInjector : DispatchingAndroidInjector<BroadcastReceiver>

    @Inject
    lateinit var sleepWidgetView : SleepWidgetView

    override fun onCreate() {
        super.onCreate()

        FirebaseCrash.setCrashCollectionEnabled(!BuildConfig.DEBUG)

        DaggerAppComponent.builder().application(this).build().inject(this)

        if (BuildConfig.DEBUG) {
            Timber.plant(DebugTree())
        } else {
            Timber.plant(FirebaseTree())
        }

        RuntimeFactory.defaultLogLevel = if (BuildConfig.DEBUG) LogLevel.FULL else LogLevel.NONE
    }

    override fun activityInjector(): AndroidInjector<Activity> = activityInjector

    override fun broadcastReceiverInjector(): AndroidInjector<BroadcastReceiver> = broadcastInjector
}