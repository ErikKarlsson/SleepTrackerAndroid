package net.erikkarlsson.simplesleeptracker

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import dagger.android.AndroidInjection
import net.erikkarlsson.simplesleeptracker.domain.SleepDetection
import javax.inject.Inject

class BootReceiver : BroadcastReceiver() {

    @Inject
    lateinit var sleepDetection: SleepDetection

    override fun onReceive(context: Context, intent: Intent) {
        AndroidInjection.inject(this, context)

        if (Intent.ACTION_BOOT_COMPLETED == intent.action) {
            sleepDetection.update()
        }
    }

}
