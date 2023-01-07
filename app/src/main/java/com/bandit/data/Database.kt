package com.bandit.data

import com.bandit.data.model.Concert
import com.bandit.helper.NavigationType

interface Database {
    val homeNavigationElementsMap: Map<String, NavigationType>
    val concerts: List<Concert>
    fun addConcert(concert: Concert): Boolean
}