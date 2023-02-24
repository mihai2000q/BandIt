package com.bandit.service

interface IPermissionService {
    fun checkReadStoragePermission(): Boolean
    fun checkCameraPermission(): Boolean
    fun requestReadStoragePermission()
    fun requestCameraPermission()
}