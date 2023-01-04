package com.bandit.mock

import com.bandit.R
import com.bandit.helper.Constants

class MockDatabase {
    val navigationViewIds get() = generateNavigationViewsSet()
    val homeNavigationElementsMap get() = mapOf(
        "Concerts" to Constants.NavigationType.Bottom,
        "Songs" to Constants.NavigationType.Bottom,
        "Chats" to Constants.NavigationType.Bottom,
        "Schedule" to Constants.NavigationType.Bottom,
    )
    private fun generateNavigationViewsSet(): Set<Int> {
        val result = mutableSetOf<Int>()
        R.id::class.java.fields.forEach {
            if(it.name.startsWith("navigation_"))
                result.add(it.getInt(null))
        }
        return result
    }
}
