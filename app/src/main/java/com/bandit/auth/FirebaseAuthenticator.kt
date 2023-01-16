package com.bandit.auth

import android.util.Log
import com.bandit.constant.Constants
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class FirebaseAuthenticator : Authenticator {
    private val _auth = Firebase.auth
    private var _currentUser = Firebase.auth.currentUser
    override val currentUser get() = _currentUser

    init {
        if(_auth.currentUser != null)
            _currentUser = _auth.currentUser
    }

    override fun signInWithEmailAndPassword(email: String, password: String): Boolean? {
        var result: Boolean? = null
        _auth.signInWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                Log.d(Constants.Firebase.AUTH_TAG, "sign in with email and password: success")
                _currentUser = _auth.currentUser
                result = true
            }
            .addOnFailureListener {
                Log.w(Constants.Firebase.AUTH_TAG, "Sign in with email and password: failed")
                result = false
            }
        return result
    }

    override fun createUser(email: String, password: String): Boolean? {
        var result: Boolean? = null
        _auth.createUserWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                Log.d(Constants.Firebase.AUTH_TAG, "create user: success")
                _currentUser = _auth.currentUser
                _currentUser?.sendEmailVerification()
                result = true
            }
            .addOnFailureListener {
                Log.w(Constants.Firebase.AUTH_TAG, "create user: failed")
                result = false
            }
        return result
    }

    override fun signOut() {
        _auth.signOut()
    }

}