package com.bandit.helper

import com.bandit.builder.AndroidBuilder
import com.bandit.builder.ConcertBuilder
import com.bandit.builder.HomeBuilder
import com.bandit.data.Database
import com.bandit.data.MockDatabase

object DILocator {
    private val androidBuilder = AndroidBuilder()
    fun getHomeBuilder(): HomeBuilder = androidBuilder
    fun getConcertBuilder(): ConcertBuilder = androidBuilder
    fun getDatabase(): Database = MockDatabase()
}
