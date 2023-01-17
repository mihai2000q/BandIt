package com.bandit.di

import com.bandit.auth.Authenticator
import com.bandit.auth.FirebaseAuthenticator
import com.bandit.builder.AndroidBuilder
import com.bandit.builder.HomeBuilder
import com.bandit.data.db.Database
import com.bandit.data.db.FirebaseDatabase

object DILocator {
    private val androidBuilder = AndroidBuilder()
    private val db = FirebaseDatabase()
    private val auth = FirebaseAuthenticator()
    val homeBuilder: HomeBuilder get() = androidBuilder
    val database: Database get() = db
    val authenticator: Authenticator get() = auth
}
