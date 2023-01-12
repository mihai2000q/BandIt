package com.bandit.data.db

import com.bandit.data.model.Concert
import com.bandit.helper.NavigationType

interface Database {
    val homeNavigationElementsMap: Map<String, NavigationType>
    val concerts: List<Concert>
    fun addConcert(concert: Concert): Boolean
    fun removeConcert(concert: Concert): Boolean
    fun editConcert(concert: Concert): Boolean
}