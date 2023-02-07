package com.bandit.model

import com.bandit.constant.BandItEnums
import com.bandit.constant.Constants
import com.bandit.data.model.Concert
import org.junit.Assert.*
import org.junit.Test
import java.time.Duration
import java.time.LocalDateTime

class ConcertTest {
    @Test
    fun concert_init() {
        val concert = Concert(
            name = "Concert1",
            dateTime = LocalDateTime.of(2020,10,25,20,30),
            duration = Duration.ofHours(1),
            bandId = -1,
            city = "Los Angeles",
            country = "United States",
            place = "Big Arena",
            concertType = BandItEnums.Concert.Type.Tournament,
        )
        assertEquals(concert.name, "Concert1")
        assertEquals(concert.dateTime, LocalDateTime.parse("2020-10-25T20:30"))
        assertEquals(concert.duration, Duration.ofHours(1))
        assertEquals(concert.city, "Los Angeles")
        assertEquals(concert.country, "United States")
        assertEquals(concert.place, "Big Arena")
        assertEquals(concert.concertType, BandItEnums.Concert.Type.Tournament)
        assertNotNull(concert.id)
        assertNotNull(concert.bandId)
    }
    @Test
    fun concert_init_id() {
        //Repeated tests improvised
        for (i in 1..100) {
            val concert = Concert.EMPTY
            assertNotNull(concert.id)
            if (concert.id < 0 || concert.id > Constants.MAX_NR_ITEMS)
                fail("The Id should be between these boundaries")
        }
    }
    @Test
    fun concert_equal() {
        val concert1 = Concert(
            name = "Concert",
            dateTime = LocalDateTime.of(2020,10,25,20,30),
            duration = Duration.ofHours(1),
            bandId = -1,
            city = "Los Angeles",
            country = "USA",
            place = "Big Arena",
            concertType = BandItEnums.Concert.Type.Tournament
        )
        val concert2 = Concert(
            name = "Concert",
            dateTime = LocalDateTime.of(2020,10,25,20,30),
            duration = Duration.ofHours(1),
            bandId = -1,
            city = "Los Angeles",
            country = "USA",
            place = "Big Arena",
            concertType = BandItEnums.Concert.Type.Tournament
        )
        val concert3 = Concert(
            name = "Concert",
            dateTime = LocalDateTime.of(2020,10,25,20,31),
            duration = Duration.ofHours(1),
            bandId = -1,
            city = "Los Angeles",
            country = "USA",
            place = "Big Arena",
            concertType = BandItEnums.Concert.Type.Tournament
        )
        assertEquals(concert1, concert2)
        assertNotEquals(concert2, concert3)
        assertNotEquals(concert1.id, concert2.id)
    }
    @Test
    fun concert_sort() {
        val outcome = mutableListOf(
            Concert(
                name = "Concert1",
                dateTime = LocalDateTime.of(2020,10,25,20,30),
                duration = Duration.ofHours(1),
                bandId = -1,
                city = "",
                country ="",
                place = "",
                concertType = BandItEnums.Concert.Type.Tournament
            ),
            Concert(
                name = "Concert2",
                dateTime = LocalDateTime.of(2021,10,25,20,30),
                duration = Duration.ofSeconds(180),
                bandId = -1,
                city = "",
                country = "",
                place = "",
                concertType = BandItEnums.Concert.Type.Tournament
            ),
            Concert(
                name = "Concert3",
                dateTime = LocalDateTime.of(2023,10,25,20,30),
                duration = Duration.ofMinutes(50),
                bandId = -1,
                city = "",
                country ="",
                place = "",
                concertType = BandItEnums.Concert.Type.Tournament
            ),
            Concert(
                name = "Concert4",
                dateTime = LocalDateTime.of(2020,1,23,20,30),
                duration = Duration.ofMinutes(50),
                bandId = -1,
                city = "",
                country ="",
                place ="",
                concertType = BandItEnums.Concert.Type.Tournament
            ),
            Concert(
                name = "Concert5",
                dateTime = LocalDateTime.of(2020,10,25,20,29),
                duration = Duration.ofSeconds(10),
                bandId = -1,
                city = "",
                country ="",
                place = "",
                concertType = BandItEnums.Concert.Type.Tournament
            )
        )
        outcome.sort()
        val expected = listOf(
            Concert(
                name = "Concert4",
                dateTime = LocalDateTime.of(2020,1,23,20,30),
                duration = Duration.ofMinutes(40),
                bandId = -1,
                city = "",
                country = "",
                place = "",
                concertType = BandItEnums.Concert.Type.Tournament
            ),
            Concert(
                name = "Concert5",
                dateTime = LocalDateTime.of(2020,10,25,20,29),
                duration = Duration.ofHours(1),
                bandId = -1,
                city = "",
                country = "",
                place = "",
                concertType = BandItEnums.Concert.Type.Tournament
            ),
            Concert(
                name = "Concert1",
                dateTime = LocalDateTime.of(2020,10,25,20,30),
                duration = Duration.ofMinutes(20),
                bandId = -1,
                city = "",
                country = "",
                place = "",
                concertType = BandItEnums.Concert.Type.Tournament
            ),
            Concert(
                name = "Concert2",
                dateTime = LocalDateTime.of(2021,10,25,20,30),
                duration = Duration.ofMinutes(10),
                bandId = -1,
                city = "",
                country = "",
                place = "",
                concertType = BandItEnums.Concert.Type.Tournament
            ),
            Concert(
                name = "Concert3",
                dateTime = LocalDateTime.of(2023,10,25,20,30),
                duration = Duration.ofMinutes(40),
                bandId = -1,
                city = "",
                country = "",
                place = "",
                concertType = BandItEnums.Concert.Type.Tournament
            )
        )
        assertEquals(outcome, expected)
    }
}