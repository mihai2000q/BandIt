package com.bandit.data.db

import com.bandit.data.model.Concert
import com.bandit.constant.BandItEnums

interface Database {
    val homeNavigationElementsMap: Map<String, BandItEnums.Home.NavigationType>
    val concerts: List<Concert>
    suspend fun init()
    suspend fun addConcert(concert: Concert)
    suspend fun removeConcert(concert: Concert)
    suspend fun editConcert(concert: Concert)
    fun clearData()
}