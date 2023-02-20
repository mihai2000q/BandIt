package com.bandit.misc

interface IPermissionChecker {
    fun checkReadStoragePermission(): Boolean
    fun checkCameraPermission(): Boolean
    fun requestReadStoragePermission()
    fun requestCameraPermission()
}