package com.bandit.di

import com.bandit.builder.AndroidBuilder
import com.bandit.builder.HomeBuilder
import com.bandit.data.db.Database
import com.bandit.data.db.FirebaseDatabase

object DILocator {
    private val androidBuilder = AndroidBuilder()
    private val database = FirebaseDatabase()
    fun getHomeBuilder(): HomeBuilder = androidBuilder
    fun getDatabase(): Database = database
}
