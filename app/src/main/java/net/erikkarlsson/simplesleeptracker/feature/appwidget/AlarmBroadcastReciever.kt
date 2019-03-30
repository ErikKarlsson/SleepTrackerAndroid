package net.erikkarlsson.simplesleeptracker.feature.appwidget

import android.app.AlarmManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import dagger.android.AndroidInjection
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import net.erikkarlsson.simplesleeptracker.domain.DateTimeProvider
import net.erikkarlsson.simplesleeptracker.domain.DetectionActionDataSource
import net.erikkarlsson.simplesleeptracker.domain.entity.ActionType
import net.erikkarlsson.simplesleeptracker.domain.entity.DetectionAction
import net.erikkarlsson.simplesleeptracker.util.hoursTo
import net.erikkarlsson.simplesleeptracker.util.offsetDateTime
import timber.log.Timber
import java.util.*
import javax.inject.Inject

private const val MAX_SNOOZE_TIME_MIN = 30.0

class AlarmBroadcastReciever: BroadcastReceiver() {

    @Inject
    lateinit var dateTimeProvider: DateTimeProvider

    @Inject
    lateinit var detectionActionDataSource: DetectionActionDataSource

    override fun onReceive(context: Context, intent: Intent) {
        AndroidInjection.inject(this, context)
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val nextAlarm = alarmManager.getNextAlarmClock()

        if (nextAlarm == null) {
//            Timber.d("Alarm Dismissed")
        } else {
            val now = dateTimeProvider.now()
            val alarmDateTime = nextAlarm.triggerTime.offsetDateTime
            val hoursToNext = now.hoursTo(alarmDateTime)
            val minutesToNext = hoursToNext * 60
            val nextAlarm = Date(nextAlarm.triggerTime).toString()

            if (minutesToNext <= MAX_SNOOZE_TIME_MIN) {
                Timber.d("Alarm SNOOZE %s %s", nextAlarm, minutesToNext)
            } else {
                Timber.d("Alarm DISMISS %s %s", nextAlarm, minutesToNext)
            }

            val action = when (minutesToNext <= MAX_SNOOZE_TIME_MIN) {
                true -> ActionType.ALARM_SNOOZE
                false -> ActionType.ALARM_DISMISS
            }

            detectionActionDataSource.insertCompletable(DetectionAction(action, now))
                    .subscribeOn(Schedulers.io())
                    .subscribeBy(
                            onComplete = { Timber.d("Alarm inserted") },
                            onError =  { Timber.e(it) }
                    )
        }

    }
}
