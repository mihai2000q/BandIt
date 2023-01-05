package com.bandit.data

import com.bandit.data.model.Concert
import com.bandit.helper.Constants

interface Database {
    val navigationViewIds: Set<Int>
    val homeNavigationElementsMap: Map<String, Constants.NavigationType>
    val concerts: List<Concert>
}