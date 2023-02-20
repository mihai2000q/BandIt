package com.bandit.util

interface IPermissionChecker {
    fun checkReadStoragePermission(): Boolean
    fun checkCameraPermission(): Boolean
    fun requestReadStoragePermission()
    fun requestCameraPermission()
}