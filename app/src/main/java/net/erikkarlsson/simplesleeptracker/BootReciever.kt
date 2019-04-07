package net.erikkarlsson.simplesleeptracker

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import dagger.android.AndroidInjection
import net.erikkarlsson.simplesleeptracker.domain.SleepDetectionScheduler
import javax.inject.Inject

class BootReceiver : BroadcastReceiver() {

    @Inject
    lateinit var sleepDetectionScheduler: SleepDetectionScheduler

    override fun onReceive(context: Context, intent: Intent) {
        AndroidInjection.inject(this, context)

        if (Intent.ACTION_BOOT_COMPLETED == intent.action) {
            sleepDetectionScheduler.schedule()
        }
    }

}
