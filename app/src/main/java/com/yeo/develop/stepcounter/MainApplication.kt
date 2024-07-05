package com.yeo.develop.stepcounter

import android.app.Application
import android.content.Intent
import android.util.Log
import com.yeo.develop.stepcounter.services.StepCounterService
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class MainApplication : Application() {
    override fun onCreate() {
        super.onCreate()
    }
}