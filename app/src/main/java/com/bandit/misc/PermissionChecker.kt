package com.bandit.misc

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import com.bandit.constant.Constants

data class PermissionChecker(
    private val activity: Activity
) : IPermissionChecker {
    override fun checkReadStoragePermission() =
        ContextCompat.checkSelfPermission(activity, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED

    override fun checkCameraPermission() =
        ContextCompat.checkSelfPermission(activity, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED

    override fun requestReadStoragePermission() =
        activity.requestPermissions(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), Constants.REQUEST_PERMISSION_STATE)

    override fun requestCameraPermission() =
        activity.requestPermissions(arrayOf(Manifest.permission.CAMERA), Constants.REQUEST_PERMISSION_STATE)
}