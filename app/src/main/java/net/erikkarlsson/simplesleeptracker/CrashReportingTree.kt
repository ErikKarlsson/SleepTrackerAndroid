package net.erikkarlsson.simplesleeptracker

import android.util.Log
import com.google.firebase.crashlytics.FirebaseCrashlytics
import timber.log.Timber


/** A tree which logs important information for crash reporting. */
class CrashReportingTree : Timber.Tree() {

    override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
        if (priority == Log.VERBOSE || priority == Log.DEBUG || priority == Log.INFO) {
            return
        }

        val crashlytics = FirebaseCrashlytics.getInstance();

        crashlytics.log(message)

        if (t == null) {
            crashlytics.recordException(Exception(message));
        } else {
            crashlytics.recordException(t);
        }
    }
}
