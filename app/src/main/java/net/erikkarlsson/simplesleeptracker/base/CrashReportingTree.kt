package net.erikkarlsson.simplesleeptracker.base

import android.util.Log
import com.crashlytics.android.Crashlytics
import timber.log.Timber


/** A tree which logs important information for crash reporting. */
class CrashReportingTree : Timber.Tree() {

    override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
        if (priority == Log.VERBOSE || priority == Log.DEBUG || priority == Log.INFO) {
            return
        }

        Crashlytics.log(priority, tag, message)

        if (t == null) {
            Crashlytics.logException(Exception(message));
        } else {
            Crashlytics.logException(t);
        }
    }
}