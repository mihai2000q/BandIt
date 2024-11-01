package com.bandit.auth

import android.util.Log
import com.bandit.constant.Constants
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.tasks.await

class FirebaseAuthenticator : Authenticator {
    override val currentUser get() = _currentUser
    private val _auth = Firebase.auth
    private var _currentUser = _auth.currentUser

    override suspend fun signInWithEmailAndPassword(email: String, password: String): Boolean? =
    coroutineScope {
        async {
            var result: Boolean? = null
            try {
                _currentUser = _auth.signInWithEmailAndPassword(email, password)
                    .await()
                    .user
                if(_currentUser != null) {
                    Log.i(
                        Constants.Firebase.Auth.TAG, "Sign in with email and password: success"
                    )
                    result = true
                }
            }
            // Incorrect combination throws an exception
            catch (_: FirebaseAuthInvalidCredentialsException) {
                Log.w(
                    Constants.Firebase.Auth.TAG, "Sign in with email and password: failed"
                )
                result = false
            }
            return@async result
        }
    }.await()

    override suspend fun createUser(email: String, password: String): Boolean? =
    coroutineScope {
        async {
            var result: Boolean? = null
            _currentUser = _auth.createUserWithEmailAndPassword(email, password)
                .addOnSuccessListener {
                    Log.i(Constants.Firebase.Auth.TAG, "create user: success")
                    _currentUser?.sendEmailVerification()
                    result = true
                }
                .addOnFailureListener {
                    Log.e(Constants.Firebase.Auth.TAG, "create user: failed")
                    result = false
                }
                .await()
                .user
            return@async result
        }
    }.await()

    override fun signOut() {
        _auth.signOut()
    }

}