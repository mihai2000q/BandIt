package com.bandit.auth

import com.google.firebase.auth.FirebaseUser

interface Authenticator {
    val currentUser: FirebaseUser?
    suspend fun signInWithEmailAndPassword(email: String, password: String): Boolean?
    suspend fun createUser(email: String, password: String): Boolean?
    suspend fun updateDisplayName(displayName: String)
    fun signOut()
}