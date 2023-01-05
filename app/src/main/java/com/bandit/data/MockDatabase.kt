package com.bandit.data

import com.bandit.R
import com.bandit.data.model.Concert
import com.bandit.helper.Constants
import java.time.LocalDate
import java.time.LocalDateTime

class MockDatabase : Database {
    override val navigationViewIds get() = generateNavigationViewsSet()
    override val homeNavigationElementsMap get() = mapOf(
        "Concerts" to Constants.NavigationType.Bottom,
        "Songs" to Constants.NavigationType.Bottom,
        "Chats" to Constants.NavigationType.Bottom,
        "Schedule" to Constants.NavigationType.Bottom,
    )
    override val concerts get() = listOf(
        Concert(
            "Legacy of the beast",
            LocalDateTime.of(2021, 10, 21, 20,0),
            "Los Angeles",
            "United States",
            "Big Arena"
        ),
        Concert(
            "Legacy of the beast 2",
            LocalDateTime.of(2021, 11, 21, 20,0),
            "Los Angeles",
            "United States of America",
            "Big Arena 2"
        ),
        Concert(
            "Legacy of the beast 3",
            LocalDateTime.of(2022, 7, 21, 20,0),
            "L A ",
            "U.S.A",
            "Big Arena 3"
        )
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
