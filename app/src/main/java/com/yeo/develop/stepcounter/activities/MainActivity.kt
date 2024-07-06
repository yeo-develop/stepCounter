package com.yeo.develop.stepcounter.activities

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.gun0912.tedpermission.PermissionListener
import com.gun0912.tedpermission.normal.TedPermission
import com.yeo.develop.stepcounter.activities.screens.StepCounterScreen
import com.yeo.develop.stepcounter.services.StepCounterService
import com.yeo.develop.stepcounter.worker.MidnightWorkerScheduler
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var workerScheduler: MidnightWorkerScheduler

    private val sdkVersionOverTiramisu = Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU
    private val sdkVersionOverS = Build.VERSION.SDK_INT >= Build.VERSION_CODES.S
    private val sdkVersionOverQ = Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            StepCounterScreen()
        }
        requestPermissions(
            onGranted = {
                startForegroundService(Intent(this, StepCounterService::class.java))
            },
            onDenied = {
                Toast.makeText(
                    this,
                    "권한이 설정되지 않아 걸음 수를 측정할 수 없어요!",
                    Toast.LENGTH_SHORT
                ).show()
            }
        )
    }

    @SuppressLint("InlinedApi")
    private fun requestPermissions(onGranted: () -> Unit, onDenied: () -> Unit) {
        val requirePermissions = when {
            sdkVersionOverTiramisu -> arrayOf(
                Manifest.permission.ACTIVITY_RECOGNITION,
                Manifest.permission.POST_NOTIFICATIONS,
                Manifest.permission.ACTIVITY_RECOGNITION
            )

            sdkVersionOverS -> {
                arrayOf(
                    Manifest.permission.SCHEDULE_EXACT_ALARM,
                    Manifest.permission.ACTIVITY_RECOGNITION
                )
            }

            sdkVersionOverQ -> arrayOf(
                Manifest.permission.ACTIVITY_RECOGNITION,
            )

            else -> emptyArray()
        }
        TedPermission.create()
            .setPermissionListener(object : PermissionListener {
                override fun onPermissionGranted() {
                    onGranted()
                }

                override fun onPermissionDenied(deniedPermissions: List<String>) {
                    onDenied()
                }
            })
            .setDeniedMessage("원활한 서비스 이용을 위해 권한을 허용해 주세요!")
            .setPermissions(
                *requirePermissions
            )
            .check()
    }

    override fun onResume() {
        super.onResume()
    }

}