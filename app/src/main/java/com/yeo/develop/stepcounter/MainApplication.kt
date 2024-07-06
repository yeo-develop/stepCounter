package com.yeo.develop.stepcounter

import android.app.Application
import android.util.Log
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import androidx.work.WorkManager
import com.yeo.develop.stepcounter.worker.AppWorkerFactory
import dagger.hilt.EntryPoint
import dagger.hilt.EntryPoints
import dagger.hilt.InstallIn
import dagger.hilt.android.HiltAndroidApp
import dagger.hilt.components.SingletonComponent
import javax.inject.Inject

@HiltAndroidApp
class MainApplication : Application() {

    @Inject
    lateinit var workerFactory: HiltWorkerFactory

    @EntryPoint
    @InstallIn(SingletonComponent::class)
    interface WorkerFactoryEntryPoint {
        fun workerFactory(): AppWorkerFactory
    }



    override fun onCreate() {
        /**
         * Configuration.Provider를 얻었기 때문에
         * Configuration.Provider를 구현하지 않는 대신 onCreate()에서 worker에 대한 구성을 설정합니다.
         * */
        val workManagerConfiguration: Configuration = Configuration.Builder()
            .setWorkerFactory(
                EntryPoints.get(this, WorkerFactoryEntryPoint::class.java).workerFactory()
            )
            .setMinimumLoggingLevel(Log.VERBOSE)
            .build()

        WorkManager.initialize(this, workManagerConfiguration)
        super.onCreate()
    }
}