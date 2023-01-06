package com.bandit.data

import com.bandit.R
import com.bandit.data.model.Concert
import com.bandit.helper.NavigationType
import java.time.LocalDateTime

class MockDatabase : Database {
    override val navigationViewIds get() = generateNavigationViewsSet()
    override val homeNavigationElementsMap get() = mapOf(
        "Concerts" to NavigationType.Bottom,
        "Songs" to NavigationType.Bottom,
        "Chats" to NavigationType.Bottom,
        "Schedule" to NavigationType.Bottom,
    )
    override val concerts = mutableListOf(
        Concert(
            "Legacy of the beast",
            LocalDateTime.of(2023, 10, 21, 20,0),
            "Los Angeles",
            "United States",
            "Big Arena",
            Concert.Type.Tournament
        ),
        Concert(
            "Legacy of the beast 2",
            LocalDateTime.of(2023, 11, 21, 20,0),
            "Los Angeles",
            "United States of America",
            "Big Arena 2",
            Concert.Type.Tournament
        ),
        Concert(
            "Legacy of the concert",
            LocalDateTime.of(2024, 7, 21, 20,0),
            "L A ",
            "U.S.A",
            "Small Arena",
            Concert.Type.Simple
        ),
        Concert(
            "Rock fest",
            LocalDateTime.of(2024, 9, 21, 23,0),
            "Berlin",
            "Germany",
            "rock fest Arena",
            Concert.Type.Festival
        ),
        Concert(
            "Amon Amar cool",
            LocalDateTime.now().plusDays(6),
            "Leipzig",
            "Germany",
            "rock fest Arena",
            Concert.Type.Simple
        )
    )

    override fun addConcert(concert: Concert): Boolean {
        return concerts.add(concert)
    }

    private fun generateNavigationViewsSet(): Set<Int> {
        val result = mutableSetOf<Int>()
        R.id::class.java.fields.forEach {
            if(it.name.startsWith("navigation_"))
                result.add(it.getInt(null))
        }
        return result
    }
}
