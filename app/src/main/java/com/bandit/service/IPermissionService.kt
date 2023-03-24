package com.bandit.service

import androidx.activity.result.ActivityResultLauncher

interface IPermissionService {
    fun checkPermission(permission: String, launcher: ActivityResultLauncher<String>, action: () -> Unit)
}