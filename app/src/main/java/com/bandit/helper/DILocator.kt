package com.bandit.helper

import com.bandit.builder.AndroidBuilder
import com.bandit.builder.HomeBuilder
import com.bandit.data.Database
import com.bandit.data.MockDatabase

object DILocator {
    private val androidBuilder = AndroidBuilder()
    private val database = MockDatabase()
    fun getHomeBuilder(): HomeBuilder = androidBuilder
    fun getDatabase(): Database = database
}
