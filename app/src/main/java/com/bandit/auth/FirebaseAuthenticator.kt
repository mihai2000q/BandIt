package com.bandit.auth

import android.util.Log
import com.bandit.constant.Constants
import com.google.firebase.auth.ktx.auth
import com.google.firebase.auth.ktx.userProfileChangeRequest
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.tasks.await

class FirebaseAuthenticator : Authenticator {
    private val _auth = Firebase.auth
    private var _currentUser = Firebase.auth.currentUser
    override val currentUser get() = _currentUser

    init {
        if(_auth.currentUser != null)
            _currentUser = _auth.currentUser
    }

    override suspend fun signInWithEmailAndPassword(email: String,
                                                    password: String): Boolean? = coroutineScope {
        async {
            var result: Boolean? = null
            try {
                _currentUser = _auth.signInWithEmailAndPassword(email, password)
                    .addOnSuccessListener {
                        Log.d(
                            Constants.Firebase.AUTH_TAG,
                            "sign in with email and password: success"
                        )
                        result = true
                    }
                    .addOnFailureListener {
                        Log.w(
                            Constants.Firebase.AUTH_TAG,
                            "Sign in with email and password: failed"
                        )
                        result = false
                    }.await().user
            }
            catch (_: Exception) {}
            return@async result
        }
    }.await()

    override suspend fun createUser(email: String, password: String, displayName: String): Boolean? {
        var result: Boolean? = null
        _currentUser = _auth.createUserWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                Log.d(Constants.Firebase.AUTH_TAG, "create user: success")
                _currentUser?.updateProfile(
                    userProfileChangeRequest {
                        this.displayName = displayName
                    }
                )
                _currentUser?.sendEmailVerification()
                result = true
            }
            .addOnFailureListener {
                Log.w(Constants.Firebase.AUTH_TAG, "create user: failed")
                result = false
            }.await().user
        return result
    }

    override fun signOut() {
        _auth.signOut()
    }

}