package com.yeo.develop.stepcounter.worker.alarm

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequest
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.yeo.develop.stepcounter.worker.MidnightWorker
import com.yeo.develop.stepcounter.worker.MidnightWorkerScheduler
import dagger.hilt.android.AndroidEntryPoint
import java.util.concurrent.TimeUnit
import javax.inject.Inject

/**
 * 시스템제약등의 이슈로 Worker가 재때 동작하지 않을 가능성을 고려해 Worker와 AlarmManager를 조합합니다.
 * */

@AndroidEntryPoint
class AlarmReceiver : BroadcastReceiver() {
    @Inject
    lateinit var workerScheduler: MidnightWorkerScheduler
    override fun onReceive(context: Context, intent: Intent?) {
        // WorkManager를 사용하여 한 번만 실행되는 작업을 설정합니다.
        val workRequest = OneTimeWorkRequestBuilder<MidnightWorker>()
            .build()

        WorkManager.getInstance(context).enqueueUniqueWork(
            "MidnightWorker",
            ExistingWorkPolicy.REPLACE,
            workRequest
        )

        workerScheduler.scheduleAlarm()
    }
}
