package com.yeo.develop.stepcounter.activities

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.yeo.develop.stepcounter.activities.screens.StepCounterScreen
import com.yeo.develop.stepcounter.activities.viewmodel.MainViewModel
import com.yeo.develop.stepcounter.services.StepCounterService
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val navController = rememberNavController()
            NavHost(navController = navController, startDestination = , builder = )
            StepCounterScreen(
                startButtonClicked = {
                    startService(Intent(this, StepCounterService::class.java))
                },
                stopButtonClicked = {
                    stopService(Intent(this, StepCounterService::class.java))
                }
            )
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.checkLocalVariableCurrentTime()
    }
}