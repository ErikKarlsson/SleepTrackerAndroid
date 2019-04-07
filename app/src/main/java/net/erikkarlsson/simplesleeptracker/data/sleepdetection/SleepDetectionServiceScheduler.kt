package net.erikkarlsson.simplesleeptracker.data.sleepdetection

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.AlarmManagerCompat
import androidx.core.content.ContextCompat
import net.erikkarlsson.simplesleeptracker.domain.*
import net.erikkarlsson.simplesleeptracker.feature.sleepdetection.AlarmReceiver
import net.erikkarlsson.simplesleeptracker.feature.sleepdetection.SleepService
import org.threeten.bp.LocalTime
import timber.log.Timber
import java.util.*
import javax.inject.Inject

class SleepDetectionServiceScheduler @Inject constructor(
        private val context: Context,
        private val alarmManager: AlarmManager,
        private val preferences: PreferencesDataSource,
        private val dateTimeProvider: DateTimeProvider) : SleepDetectionScheduler {

    override fun schedule() {
        cancelAlarms()

        if (!isSleepDetectionEnabled()) {
            stopService()
            return
        }

        val startTime = getStartTime()
        val stopTime = getStopTime()
        val now = dateTimeProvider.now().toLocalTime()

        setAlarms(startTime, stopTime)

        if (isOpen(startTime, stopTime, now)) {
            startService()
        } else {
            stopService()
        }
    }

    override fun startDetection() {
        Timber.d("startDetection")
        resetAlarms()
        startService()
    }

    override fun stopDetection() {
        Timber.d("stopDetection")
        resetAlarms()
        stopService()
    }

    private fun resetAlarms() {
        cancelAlarms()
        setAlarms(getStartTime(), getStopTime())
    }

    private fun isSleepDetectionEnabled(): Boolean =
            preferences.getBoolean(PREFS_SLEEP_DETECTION_ENABLED).blockingFirst()

    private fun getStartTime(): LocalTime {
        val startTimeString = preferences.getString(PREFS_SLEEP_DETECTION_START_TIME).blockingFirst()
        return LocalTime.parse(startTimeString)
    }

    private fun getStopTime(): LocalTime {
        val stopTimeString = preferences.getString(PREFS_SLEEP_DETECTION_STOP_TIME).blockingFirst()
        return LocalTime.parse(stopTimeString)
    }

    private fun setAlarms(startTime: LocalTime, stopTime: LocalTime) {
        setStartDetectionAlarm(startTime)
        setStopDetectionAlarm(stopTime)
    }

    private fun cancelAlarms() {
        alarmManager.cancel(getStartDetectionPendingIntent())
        alarmManager.cancel(getStopDetectionPendingIntent())
    }

    private fun startService() {
        val service = Intent(context, SleepService::class.java)
        service.setAction(SleepService.START_FOREGROUND_ACTION)
        ContextCompat.startForegroundService(context, service)
    }

    private fun stopService() {
        val service = Intent(context, SleepService::class.java)
        service.setAction(SleepService.STOP_FOREGROUND_ACTION)
        context.startService(service)
    }

    private fun setStartDetectionAlarm(time: LocalTime) {
        val intent = getStartDetectionPendingIntent()
        val timestamp = setAlarm(intent, time)
        preferences.set(PREFS_SLEEP_DETECTION_ALARM_START_DATE, timestamp) // It's not possible to query alarms from the alarm manager after creating them, persist any needed data.
    }

    private fun setStopDetectionAlarm(time: LocalTime) {
        val intent = getStopDetectionPendingIntent()
        val timestamp = setAlarm(intent, time)
        preferences.set(PREFS_SLEEP_DETECTION_ALARM_STOP_DATE, timestamp)
    }

    private fun isOpen(start: LocalTime, end: LocalTime, time: LocalTime): Boolean =
            if (start.isAfter(end)) {
                !time.isBefore(start) || !time.isAfter(end)
            } else {
                !time.isBefore(start) && !time.isAfter(end)
            }

    private fun getStopDetectionPendingIntent(): PendingIntent =
            getPendingIntent(STOP_SLEEP_DETECTION_ACTION)

    private fun getStartDetectionPendingIntent(): PendingIntent =
            getPendingIntent(START_SLEEP_DETECTION_ACTION)

    private fun getPendingIntent(action: String): PendingIntent {
        val intent = Intent(context, AlarmReceiver::class.java)
        intent.setAction(action)

        return PendingIntent.getBroadcast(context,
                0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
    }

    private fun setAlarm(pendingIntent: PendingIntent, time: LocalTime): Long {
        val now = System.currentTimeMillis()
        val calendar = Calendar.getInstance().apply {
            timeInMillis = now
            set(Calendar.HOUR_OF_DAY, time.hour)
            set(Calendar.MINUTE, time.minute)
            set(Calendar.SECOND, 0)
        }

        val alarmTimestamp = when (calendar.getTimeInMillis() <= now) {
            true -> calendar.getTimeInMillis() + (AlarmManager.INTERVAL_DAY + 1);
            false -> calendar.timeInMillis
        }

        AlarmManagerCompat.setExactAndAllowWhileIdle(
                alarmManager,
                AlarmManager.RTC_WAKEUP,
                alarmTimestamp,
                pendingIntent)

        return alarmTimestamp
    }

    companion object {
        const val START_SLEEP_DETECTION_ACTION = "net.erikkarlsson.simplesleeptracker.START_SLEEP_DETECTION_ACTION"
        const val STOP_SLEEP_DETECTION_ACTION = "net.erikkarlsson.simplesleeptracker.STOP_SLEEP_DETECTION_ACTION"
    }

}
