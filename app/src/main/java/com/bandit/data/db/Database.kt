package com.bandit.data.db

import com.bandit.data.model.Concert
import com.bandit.constant.BandItEnums

interface Database {
    val homeNavigationElementsMap: Map<String, BandItEnums.Home.NavigationType>
    val concerts: List<Concert>
    suspend fun init()
    suspend fun add(item: Any)
    suspend fun remove(item: Any)
    suspend fun edit(item: Any)
    fun clearData()
}