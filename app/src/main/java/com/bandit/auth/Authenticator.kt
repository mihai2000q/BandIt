package com.bandit.auth

import com.google.firebase.auth.FirebaseUser

interface Authenticator {
    /**
     * This property represents the current user that is logged in
     */
    val currentUser: FirebaseUser?
    /**
     * This method is used to sign in with an email and a password
     * @param email the given email
     * @param password the given password
     * @return
     * - true, when the user logged in successfully
     * - false, when the user entered the wrong credentials
     * - null, in case something goes wrong (asynchronous programming problems)
     */
    suspend fun signInWithEmailAndPassword(email: String, password: String): Boolean?
    /**
     * This method is used to create a new user
     * @param email the given email
     * @param password the given password
     * @return
     * - true, when the user got created successfully
     * - false, when there is already an user with those credentials
     * - null, in case something goes wrong (asynchronous programming problems)
     */
    suspend fun createUser(email: String, password: String): Boolean?
    /**
     * This method signs out the current user and clears the disk cache
     */
    fun signOut()
}