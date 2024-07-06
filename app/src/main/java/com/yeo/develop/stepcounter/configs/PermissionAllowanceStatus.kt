package com.yeo.develop.stepcounter.configs

import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionStatus
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.shouldShowRationale

/**
 * 권한 상태를 간단히 비교하기 위해 생성한 enum입니다.
 * */
sealed class PermissionAllowanceStatus {
    data object Granted : PermissionAllowanceStatus()
    data object ShouldShowRationale: PermissionAllowanceStatus()
    data object DoNotShowAgain: PermissionAllowanceStatus()
}

@OptIn(ExperimentalPermissionsApi::class)
fun PermissionStatus.toPermissionAllowanceStatus(): PermissionAllowanceStatus {
    return when {
        isGranted -> PermissionAllowanceStatus.Granted
        shouldShowRationale -> PermissionAllowanceStatus.ShouldShowRationale
        else -> PermissionAllowanceStatus.DoNotShowAgain
    }
}