package com.bandit.data.db

import com.bandit.data.model.Concert
import com.bandit.helper.BandItEnums
import java.time.LocalDateTime

class MockDatabase : Database {
    override val homeNavigationElementsMap get() = mapOf(
        "Concerts" to BandItEnums.Home.NavigationType.Bottom,
        "Songs" to BandItEnums.Home.NavigationType.Bottom,
        "Chats" to BandItEnums.Home.NavigationType.Bottom,
        "Schedule" to BandItEnums.Home.NavigationType.Bottom,
    )
    override val concerts = mutableListOf(
        Concert(
            "Legacy of the beast",
            LocalDateTime.of(2023, 10, 21, 20,0),
            "Los Angeles",
            "United States",
            "Big Arena",
            BandItEnums.Concert.Type.Tournament
        ),
        Concert(
            "Legacy of the beast 2",
            LocalDateTime.of(2023, 11, 21, 20,0),
            "Los Angeles",
            "United States of America",
            "Big Arena 2",
            BandItEnums.Concert.Type.Tournament
        ),
        Concert(
            "Legacy of the concert",
            LocalDateTime.of(2024, 7, 21, 20,0),
            "L A ",
            "U.S.A",
            "Small Arena",
            BandItEnums.Concert.Type.Simple
        ),
        Concert(
            "Rock fest",
            LocalDateTime.of(2024, 9, 21, 23,0),
            "Berlin",
            "Germany",
            "rock fest Arena",
            BandItEnums.Concert.Type.Festival
        ),
        Concert(
            "Amon Amar cool",
            LocalDateTime.now().plusDays(6),
            "Leipzig",
            "Germany",
            "rock fest Arena",
            BandItEnums.Concert.Type.Simple
        )
    )

    override fun addConcert(concert: Concert): Boolean {
        return concerts.add(concert)
    }

    override fun removeConcert(concert: Concert): Boolean {
        return concerts.remove(concert)
    }

    override fun editConcert(concert: Concert): Boolean {
        TODO("Not yet implemented")
    }
}
