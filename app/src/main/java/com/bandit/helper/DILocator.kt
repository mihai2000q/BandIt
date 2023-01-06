package com.bandit.helper

import com.bandit.builder.AndroidBuilder
import com.bandit.builder.ConcertBuilder
import com.bandit.builder.HomeBuilder
import com.bandit.data.Database
import com.bandit.data.MockDatabase

object DILocator {
    fun getHomeBuilder(): HomeBuilder = AndroidBuilder()
    fun getConcertBuilder(): ConcertBuilder = AndroidBuilder()
    fun getDatabase(): Database = MockDatabase()
}
