package com.yeo.develop.stepcounter.activities

import android.Manifest
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import com.gun0912.tedpermission.PermissionListener
import com.gun0912.tedpermission.normal.TedPermission
import com.yeo.develop.stepcounter.activities.screens.StepCounterScreen
import com.yeo.develop.stepcounter.activities.viewmodel.MainViewModel
import com.yeo.develop.stepcounter.services.StepCounterService
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val sdkVersionOverTiramisu = Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU
    private val sdkVersionOverQ = Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q

    private val viewModel: MainViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            StepCounterScreen(
                startButtonClicked = {
                    requestPermissions(
                        onGranted = {
                            startService(Intent(this@MainActivity, StepCounterService::class.java))
                        },
                        onDenied = {
                            Toast.makeText(this, "권한이 허용되지 않아 거리적산을 시작할 수 없어요", Toast.LENGTH_SHORT)
                                .show()
                        }
                    )

                },
                stopButtonClicked = {
                    stopService(Intent(this@MainActivity, StepCounterService::class.java))
                }
            )
        }
    }

    private fun requestPermissions(onGranted: () -> Unit, onDenied: () -> Unit) {
        TedPermission.create()
            .setPermissionListener(object : PermissionListener {
                override fun onPermissionGranted() {
                    onGranted()
                }

                override fun onPermissionDenied(deniedPermissions: List<String>) {
                    onDenied()
                }
            })
            .setDeniedMessage("원활한 서비스 이용을 위해 권한을 허용해주세요!")
            .setPermissions(
                when {
                    sdkVersionOverQ -> {
                        Manifest.permission.ACTIVITY_RECOGNITION
                    }
                    sdkVersionOverTiramisu -> {
                        Manifest.permission.ACTIVITY_RECOGNITION
                        Manifest.permission.POST_NOTIFICATIONS
                    }
                    else -> {
                        //Q 미만 기기에선 권한을 물어볼 필요가 없습니다!
                        return
                    }
                }
            )
            .check()
    }

    override fun onResume() {
        super.onResume()
        viewModel.checkLocalVariableCurrentTime()
    }

}