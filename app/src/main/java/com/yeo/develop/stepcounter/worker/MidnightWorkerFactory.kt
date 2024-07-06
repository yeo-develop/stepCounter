package com.yeo.develop.stepcounter.worker

import android.content.Context
import androidx.work.DelegatingWorkerFactory
import androidx.work.ListenableWorker
import androidx.work.WorkerFactory
import androidx.work.WorkerParameters
import com.yeo.develop.stepcounter.datastore.AppDataStore
import javax.inject.Inject
import javax.inject.Singleton

/**
 * hilt worker annotation을 통한 자동생성에 애로사항이 있어 만든 factory입니다.
 * */
class MidnightWorkerFactory @Inject constructor(
    private val appDataStore: AppDataStore,
) : WorkerFactory() {
    override fun createWorker(appContext: Context, workerClassName: String, workerParameters: WorkerParameters): ListenableWorker? {
        return when (workerClassName) {
            MidnightWorker::class.java.name -> MidnightWorker(appContext, workerParameters,appDataStore)
            else -> null
        }
    }
}

@Singleton
class AppWorkerFactory @Inject constructor(
    midnightWorkerFactory: MidnightWorkerFactory
) : DelegatingWorkerFactory() {
    init {
        addFactory(midnightWorkerFactory)
    }
}
