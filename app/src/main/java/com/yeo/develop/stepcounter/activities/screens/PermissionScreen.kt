package com.yeo.develop.stepcounter.activities.screens

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.yeo.develop.stepcounter.configs.PermissionAllowanceStatus
import com.yeo.develop.stepcounter.configs.toPermissionAllowanceStatus
import com.yeo.develop.stepcounter.ui.Footer


@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun PermissionScreen(onPermissionGranted: () -> Unit) {

    val context = LocalContext.current
    val checkSdkOverTiramisu = Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU
    val checkSdkOverQ = Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q

    /**
     * 권한 최초 거절 시 보여주는 Dialog State입니다.
     * Pair (isVisible, 요청 대상 permission) 형식으로 되어있습니다.
     * */
    var permissionRetryDialogState by remember { mutableStateOf(Pair(false, "")) }

    /**
     * 권한이 "다시묻지 않음" 처리 되었을 시 보여주는 Dialog State입니다.
     * 위와 마찬가지로 Pair (isVisible, 요청 대상 permission) 형식으로 되어있습니다.
     * */
    var permissionSettingDialogState by remember { mutableStateOf(Pair(false, "")) }

    /**
     * SDK 버전별로 요청해야할 권한이 다르니 분기처리를 진행합니다.
     * SDK 33 - POST_NOTIFICATION, ACTIVITY_RECOGNITION
     * SDK 29 - ACTIVITY_RECOGNITION
     * 29 미만 - EMPTY
     * */

    val multiplePermissionsState = rememberMultiplePermissionsState(
        if (checkSdkOverTiramisu) {
            listOf(
                android.Manifest.permission.POST_NOTIFICATIONS,
                android.Manifest.permission.ACTIVITY_RECOGNITION
            )
        } else if (checkSdkOverQ) {
            listOf(android.Manifest.permission.ACTIVITY_RECOGNITION)
        } else {
            emptyList()
        }
    )

    /**
     * 권한이 모두 허용된경우나 list가 비어있는 경우(API 29 이하)
     * view를 그려줄 필요가 없으므로 바로 onPermissionGranted를 호출해주도록 합시다.
     * */
    if (multiplePermissionsState.permissions.all { it.status.isGranted } || multiplePermissionsState.permissions.isEmpty()) {
        onPermissionGranted()
    } else {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Spacer(modifier = Modifier.weight(1f))
            Text(text = "앱을 사용하기 위해 아래의 권한이 필요해요", fontSize = 20.sp)
            Spacer(modifier = Modifier.weight(1f))
            Column(
                modifier = Modifier
                    .fillMaxWidth(0.8f)
                    .fillMaxHeight(0.5f)
            ) {
                when {
                    checkSdkOverTiramisu -> {
                        PermissionDescriptionView(
                            title = "활동",
                            description = "걸음 수 측정을 위해 필요해요."
                        )
                        Spacer(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(20.dp)
                        )
                        PermissionDescriptionView(
                            title = "알림",
                            description = "앱이 사용 중이 아닐 때도 걸음 수를 인지시켜드리기 위해 필요해요."
                        )
                    }

                    checkSdkOverQ -> {
                        PermissionDescriptionView(
                            title = "활동",
                            description = "걸음 수 측정을 위해 필요해요."
                        )
                    }

                    else -> {
                        /*no-op*/
                    }
                }
            }
            Spacer(modifier = Modifier.weight(1f))
            Footer(
                title = "다음",
                onClick = {
                    multiplePermissionsState.launchMultiplePermissionRequest()
                }
            )

            /**
             * 이하 권한 핸들러
             * */
            multiplePermissionsState.permissions.forEach { permissionState ->
                when (permissionState.status.toPermissionAllowanceStatus()) {
                    PermissionAllowanceStatus.Granted -> {
                        /** no-op **/
                    }

                    PermissionAllowanceStatus.ShouldShowRationale -> {
                        permissionRetryDialogState = Pair(true, permissionState.permission)
                    }

                    PermissionAllowanceStatus.DoNotShowAgain -> {
                        permissionSettingDialogState = Pair(true, permissionState.permission)
                    }
                }
            }
        }

        if (permissionRetryDialogState.first) {
            PermissionRetryDialog(
                onDismiss = {
                    permissionRetryDialogState = Pair(false, "")
                },
                onRequestPermission = {
                    multiplePermissionsState.permissions.first { it.permission == permissionRetryDialogState.second }
                        .launchPermissionRequest()
                },
                description = when (permissionRetryDialogState.second) {
                    Manifest.permission.ACTIVITY_RECOGNITION ->
                        "걸음 수 측정 을 위해 활동 인식 권한이 필요합니다.\n권한을 허용해 주세요."

                    Manifest.permission.POST_NOTIFICATIONS ->
                        "앱을 사용하지 않을때에도 걸음 수 측정을 위해선 알림 권한이 필요합니다.\n권한을 허용해 주세요."

                    else -> ""
                }
            )
        }

        if (permissionSettingDialogState.first) {
            PermissionSettingsDialog(
                onDismiss = {
                    permissionSettingDialogState = Pair(false, "")
                },
                onOpenSettings = {
                    context.startActivity(
                        Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                            data = Uri.fromParts("package", context.packageName, null)
                        }
                    )
                },
                description = when (permissionRetryDialogState.second) {
                    Manifest.permission.ACTIVITY_RECOGNITION ->
                        "활동 인식 권한이 거절 되었습니다.\n설정에서 권한을 허용해 주세요."

                    Manifest.permission.POST_NOTIFICATIONS ->
                        "알림 권한이 거절 되었습니다.\n설정에서 권한을 허용해 주세요."

                    else -> ""
                }
            )

        }
    }
}


@Composable
private fun PermissionDescriptionView(title: String, description: String) {
    Column {
        Text(
            text = title,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold
        )

        Text(
            fontSize = 16.sp,
            text = description
        )
    }
}

/**
 * 권한이 1번 거절되었을때 띄워지는 dialog입니다. ( 다시 묻지 않음 처리 x )
 * */
@Composable
fun PermissionRetryDialog(
    onDismiss: () -> Unit,
    onRequestPermission: () -> Unit,
    description: String,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = "권한 안내") },
        text = { Text(text = description) },
        confirmButton = {
            TextButton(onClick = onRequestPermission) {
                Text(text = "권한 요청")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(text = "취소")
            }
        }
    )
}

/**
 * 권한이 "다시 묻지 않음" 처리되었을때 띄워지는 dialog입니다. ( 다시 묻지 않음 처리 x )
 * */
@Composable
fun PermissionSettingsDialog(
    onDismiss: () -> Unit,
    onOpenSettings: () -> Unit,
    description: String
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = "권한 안내") },
        text = { Text(text = description) },
        confirmButton = {
            TextButton(onClick = onOpenSettings) {
                Text(text = "설정 열기")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(text = "취소")
            }
        }
    )
}


@Composable
@Preview(showBackground = true)
private fun PermissionScreenPreview() {
    PermissionScreen {}
}

@Composable
@Preview(showBackground = true)
private fun PermissionDescriptionViewPreview() {
    PermissionDescriptionView(title = "중요한 권한", description = "앱을 쓰기위해 필요해요")
}