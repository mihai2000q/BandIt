package com.bandit.concerts

import com.bandit.data.model.Concert
import com.bandit.constant.BandItEnums
import com.bandit.constant.Constants
import org.junit.Test

import org.junit.Assert.*
import java.time.LocalDateTime

class ConcertTest {
    @Test
    fun concert_init() {
        val concert = Concert(
            "Concert1",
            LocalDateTime.of(2020,10,25,20,30),
            "Los Angeles",
            "United States",
            "Big Arena",
            BandItEnums.Concert.Type.Tournament
        )
        assertEquals(concert.name, "Concert1")
        assertEquals(concert.dateTime, LocalDateTime.parse("2020-10-25T20:30"))
        assertEquals(concert.city, "Los Angeles")
        assertEquals(concert.country, "United States")
        assertEquals(concert.place, "Big Arena")
        assertEquals(concert.type, BandItEnums.Concert.Type.Tournament)
    }
    @Test
    fun concert_init_id() {
        //Repeated tests improvised
        for (i in 1..100) {
            val concert = Concert.getEmpty()
            assertNotNull(concert.id)
            if (concert.id < 0 || concert.id > Constants.MAX_NR_ITEMS)
                fail("The Id should be between these boundaries")
        }
    }
    @Test
    fun concert_equal() {
        val concert1 = Concert(
            "Concert",
            LocalDateTime.of(2020,10,25,20,30),
            "Los Angeles",
            "USA",
            "Big Arena",
            BandItEnums.Concert.Type.Tournament
        )
        val concert2 = Concert(
            "Concert",
            LocalDateTime.of(2020,10,25,20,30),
            "Los Angeles",
            "USA",
            "Big Arena",
            BandItEnums.Concert.Type.Tournament
        )
        assertEquals(concert1, concert2)
        assertNotEquals(concert1.id, concert2.id)
    }
    @Test
    fun concert_sort() {
        val outcome = mutableListOf(
            Concert(
                "Concert1",
                LocalDateTime.of(2020,10,25,20,30),
                "",
                "",
                "",
                BandItEnums.Concert.Type.Tournament
            ),
            Concert(
                "Concert2",
                LocalDateTime.of(2021,10,25,20,30),
                "",
                "",
                "",
                BandItEnums.Concert.Type.Tournament
            ),
            Concert(
                "Concert3",
                LocalDateTime.of(2023,10,25,20,30),
                "",
                "",
                "",
                BandItEnums.Concert.Type.Tournament
            ),
            Concert(
                "Concert4",
                LocalDateTime.of(2020,1,23,20,30),
                "",
                "",
                "",
                BandItEnums.Concert.Type.Tournament
            ),
            Concert(
                "Concert5",
                LocalDateTime.of(2020,10,25,20,29),
                "",
                "",
                "",
                BandItEnums.Concert.Type.Tournament
            ),
            Concert(
                "Concert6",
                LocalDateTime.of(2020,10,25,19,29),
                "",
                "",
                "",
                BandItEnums.Concert.Type.Tournament
            )
        )
        outcome.sort()
        val expected = listOf(
            Concert(
                "Concert4",
                LocalDateTime.of(2020,1,23,20,30),
                "",
                "",
                "",
                BandItEnums.Concert.Type.Tournament
            ),
            Concert(
                "Concert6",
                LocalDateTime.of(2020,10,25,19,29),
                "",
                "",
                "",
                BandItEnums.Concert.Type.Tournament
            ),
            Concert(
                "Concert5",
                LocalDateTime.of(2020,10,25,20,29),
                "",
                "",
                "",
                BandItEnums.Concert.Type.Tournament
            ),
            Concert(
                "Concert1",
                LocalDateTime.of(2020,10,25,20,30),
                "",
                "",
                "",
                BandItEnums.Concert.Type.Tournament
            ),
            Concert(
                "Concert2",
                LocalDateTime.of(2021,10,25,20,30),
                "",
                "",
                "",
                BandItEnums.Concert.Type.Tournament
            ),
            Concert(
                "Concert3",
                LocalDateTime.of(2023,10,25,20,30),
                "",
                "",
                "",
                BandItEnums.Concert.Type.Tournament
            )
        )
        assertEquals(outcome, expected)
    }
}