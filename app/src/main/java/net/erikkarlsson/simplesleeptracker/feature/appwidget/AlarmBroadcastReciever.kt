package net.erikkarlsson.simplesleeptracker.feature.appwidget

import android.app.AlarmManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import dagger.android.AndroidInjection
import timber.log.Timber
import java.util.*


class AlarmBroadcastReciever: BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        AndroidInjection.inject(this, context)
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val nextAlarm = alarmManager.getNextAlarmClock()
        val date = Date(nextAlarm.triggerTime)


        if (nextAlarm == null) {
            Timber.d("Alarm changed null")
        } else {
            Timber.d("Alarm changed nextAlarm: %s", Date(nextAlarm.triggerTime).toString())
        }
    }
}
