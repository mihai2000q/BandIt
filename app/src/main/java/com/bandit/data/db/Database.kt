package com.bandit.data.db

import com.bandit.data.model.Concert
import com.bandit.constant.BandItEnums

interface Database {
    val homeNavigationElementsMap: Map<String, BandItEnums.Home.NavigationType>
    val concerts: List<Concert>
    fun addConcert(concert: Concert)
    fun removeConcert(concert: Concert)
    fun editConcert(concert: Concert)
    suspend fun init()
}