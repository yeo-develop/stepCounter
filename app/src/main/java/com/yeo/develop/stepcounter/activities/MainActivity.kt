package com.yeo.develop.stepcounter.activities

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.sp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.gun0912.tedpermission.PermissionListener
import com.gun0912.tedpermission.normal.TedPermission
import com.yeo.develop.stepcounter.R
import com.yeo.develop.stepcounter.activities.screens.StepCounterScreen
import com.yeo.develop.stepcounter.activities.screens.WalkingHistoryScreen
import com.yeo.develop.stepcounter.services.StepCounterService
import com.yeo.develop.stepcounter.ui.PrimaryBlue
import com.yeo.develop.stepcounter.worker.MidnightWorkerScheduler
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var workerScheduler: MidnightWorkerScheduler

    private val sdkVersionOverTiramisu = Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU
    private val sdkVersionOverQ = Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val navController = rememberNavController()
            /**
             * Scaffold를 사용해 하단에 들어갈 navigationBar 만큼의 padding을 갖고 NavHost를 그려주도록 합니다.
             * */
            Scaffold(bottomBar = {
                BottomAppBar { route ->
                    navController.navigate(route) {
                        popUpTo(navController.graph.id) {
                            inclusive = true
                        }
                    }
                }
            }) { bottomPadding ->
                NavHost(
                    navController = navController,
                    startDestination = BottomNavigationScreenConfigs.Home.screenRoute,
                    modifier = Modifier.padding(bottomPadding)
                ) {
                    composable(BottomNavigationScreenConfigs.Home.screenRoute) {
                        StepCounterScreen()
                    }
                    composable(BottomNavigationScreenConfigs.WalkingHistory.screenRoute) {
                        WalkingHistoryScreen()
                    }
                }
            }
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
                Manifest.permission.POST_NOTIFICATIONS
            )

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

    /**
     * 앱 하단에 들어갈 내비게이션 바입니다
     * */
    @Composable
    private fun BottomAppBar(
        onIndexClicked: (String) -> Unit
    ) {
        val navigationList = listOf(
            BottomNavigationScreenConfigs.Home,
            BottomNavigationScreenConfigs.WalkingHistory
        )
        BottomAppBar(
            modifier = Modifier.fillMaxWidth(),
            contentColor = PrimaryBlue
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                navigationList.forEach { item ->
                    NavigationItem(item) { route ->
                        onIndexClicked(route)
                    }
                }
            }
        }
    }

    /**
     * 내비게이션 바에 들어갈 아이템들입니다.
     * */
    @Composable
    fun NavigationItem(config: BottomNavigationScreenConfigs, onClickItem: (String) -> Unit) {
        Button(
            onClick = { onClickItem(config.screenRoute) },
            colors = ButtonDefaults.buttonColors(Color.Transparent)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Image(
                    painter = painterResource(id = config.icon),
                    contentDescription = config.title,
                    colorFilter = ColorFilter.tint(PrimaryBlue)
                )
                Text(
                    text = config.title, fontSize = 16.sp,
                    color = PrimaryBlue
                )
            }
        }
    }

    sealed class BottomNavigationScreenConfigs(
        val title: String,
        val screenRoute: String,
        @DrawableRes val icon: Int
    ) {
        data object Home : BottomNavigationScreenConfigs("메인", "main", R.drawable.ic_home)
        data object WalkingHistory :
            BottomNavigationScreenConfigs("걷기 기록", "walking_history", R.drawable.ic_walk_history)
    }

}