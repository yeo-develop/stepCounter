package com.yeo.develop.stepcounter.worker

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.yeo.develop.stepcounter.worker.alarm.AlarmReceiver
import dagger.hilt.android.qualifiers.ApplicationContext
import java.time.Duration
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.time.temporal.ChronoUnit
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class MidnightWorkerScheduler @Inject constructor(
    @ApplicationContext private val context: Context
) {

    /**
     * 매일 자정 걸음을 초기화하는 작업을 스케쥴링합니다.
     * */
    @SuppressLint("ScheduleExactAlarm")
    fun scheduleAlarm() {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, AlarmReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        //정확히 00시 00분에 Worker가 일을할 수 있도록 알람을 맞춰줍니다
        val midnight =
            LocalDateTime.now().plusDays(1).withHour(0).withMinute(0).withSecond(0).withNano(0)
        Log.d("Scheduler", "Scheduled! alarm will be goes off $midnight ")
        val triggerTime = midnight.atZone(ZoneId.systemDefault()).toEpochSecond() * 1000

        alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerTime, pendingIntent)
    }
}