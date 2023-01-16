package com.bandit.auth

import com.google.firebase.auth.FirebaseUser

interface Authenticator {
    val currentUser: FirebaseUser?
    fun signInWithEmailAndPassword(email: String, password: String): Boolean?
    fun createUser(email: String, password: String, displayName: String): Boolean?
    fun signOut()
}