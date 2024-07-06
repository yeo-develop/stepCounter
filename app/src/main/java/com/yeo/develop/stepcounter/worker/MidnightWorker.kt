package com.yeo.develop.stepcounter.worker

import android.content.Context
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.yeo.develop.stepcounter.datastore.AppDataStore
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

/**
 * 자정마다 dataStore의 총 걸음 변수를 초기화해주는 worker입니다.
 * */
@HiltWorker
class MidnightWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParams: WorkerParameters,
    private val appDataStore: AppDataStore
) : CoroutineWorker(context, workerParams) {
    override suspend fun doWork(): Result {
        appDataStore.dailyTotalSteps = 0
        Log.d("WORKER", "doWork!!!!!!!")
        return Result.success()
    }
}