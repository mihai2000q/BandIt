package com.bandit.service.impl

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import androidx.activity.result.ActivityResultLauncher
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.bandit.R
import com.bandit.service.IPermissionService
import com.bandit.ui.component.AndroidComponents

data class PermissionService(
    private val activity: Activity
) : IPermissionService {
    override fun checkPermission(
        permission: String,
        launcher: ActivityResultLauncher<String>,
        action: () -> Unit
    ) {
        if(this.isPermissionGranted(permission)) {
            action()
            return
        }
        if (ActivityCompat.shouldShowRequestPermissionRationale(activity, permission))
            AndroidComponents.alertDialog(
                activity,
                activity.resources.getString(R.string.alert_dialog_permission_title),
                activity.resources.getString(R.string.alert_dialog_permission_message),
                activity.resources.getString(R.string.alert_dialog_positive),
                activity.resources.getString(R.string.alert_dialog_negative),
            ) {
                launcher.launch(permission)
            }
        else
            launcher.launch(permission)
    }
    private fun isPermissionGranted(permission: String) =
        ContextCompat.checkSelfPermission(activity, permission) == PackageManager.PERMISSION_GRANTED

}