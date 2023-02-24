package com.bandit.di

import android.app.Activity
import com.bandit.auth.Authenticator
import com.bandit.auth.FirebaseAuthenticator
import com.bandit.data.db.Database
import com.bandit.data.db.FirebaseDatabase
import com.bandit.storage.FirebaseStorage
import com.bandit.storage.Storage
import com.bandit.service.IPermissionService
import com.bandit.service.IValidatorService
import com.bandit.service.impl.PermissionService
import com.bandit.service.impl.ValidatorService

object DILocator {
    private val database = FirebaseDatabase()
    private val authenticator = FirebaseAuthenticator()
    private val storage = FirebaseStorage()
    fun getDatabase(): Database = database
    fun getAuthenticator(): Authenticator = authenticator
    fun getStorage(): Storage = storage
    fun getPermissionService(activity: Activity): IPermissionService = PermissionService(activity)
    fun getValidatorService(activity: Activity): IValidatorService = ValidatorService(activity)
}
