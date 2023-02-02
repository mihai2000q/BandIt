package com.bandit.model

import com.bandit.data.model.Concert
import com.bandit.constant.BandItEnums
import com.bandit.constant.Constants
import org.junit.Test

import org.junit.Assert.*
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
    @Test
    fun concert_isOutdated() {
        val concert1 = Concert(
            name = "",
            dateTime = LocalDateTime.now().minusSeconds(1),
            duration = Duration.ofMinutes(10),
            bandId = -1,
            city = "",
            country = "",
            place = "",
            concertType = BandItEnums.Concert.Type.Tournament
        )
        val concert2 = Concert(
            name = "",
            dateTime = LocalDateTime.now().plusSeconds(1),
            duration = Duration.ofMinutes(10),
            bandId = -1,
            city = "",
            country = "",
            place = "",
            concertType = BandItEnums.Concert.Type.Tournament
        )
        assertTrue(concert1.isOutdated())
        assertTrue(!concert2.isOutdated())
    }
    @Test
    fun concert_is24HoursApart() {
        val concert1 = Concert(
            name = "",
            dateTime =LocalDateTime.now().plusHours(3),
            duration = Duration.ofMinutes(100),
            bandId = -1,
            city = "",
            country = "",
            place = "",
            concertType = BandItEnums.Concert.Type.Tournament
        )
        val concert2 = Concert(
            name = "",
            dateTime = LocalDateTime.now().plusHours(25),
            duration = Duration.ofMinutes(100),
            bandId =  -1,
            city = "",
            country ="",
            place = "",
            concertType = BandItEnums.Concert.Type.Tournament
        )
        val concert3 = Concert(
            name = "",
            dateTime = LocalDateTime.now().plusHours(23).plusMinutes(59),
            duration = Duration.ofMinutes(120),
            bandId = -1,
            city = "",
            country = "",
            place = "",
            concertType = BandItEnums.Concert.Type.Tournament
        )
        assertTrue(concert1.is24HoursApart())
        assertTrue(!concert2.is24HoursApart())
        assertTrue(concert3.is24HoursApart())
    }
    @Test
    fun concert_is7DaysApart() {
        val concert1 = Concert(
            name = "",
            dateTime = LocalDateTime.now().plusDays(3),
            duration = Duration.ofHours(3),
            bandId =  -1,
            city = "",
            country = "",
            place = "",
            concertType = BandItEnums.Concert.Type.Tournament
        )
        val concert2 = Concert(
            name = "",
            dateTime = LocalDateTime.now().plusDays(8),
            duration = Duration.ofHours(100),
            bandId = -1,
            city = "",
            country = "",
            place = "",
            concertType = BandItEnums.Concert.Type.Tournament
        )
        val concert3 = Concert(
            name = "",
            dateTime = LocalDateTime.now().plusDays(6).plusHours(23).plusMinutes(59),
            duration = Duration.ofMinutes(98),
            bandId = -1,
            city = "",
            country = "",
            place = "",
            concertType = BandItEnums.Concert.Type.Tournament
        )
        assertTrue(concert1.is7DaysApart())
        assertTrue(!concert2.is7DaysApart())
        assertTrue(concert3.is7DaysApart())
    }
    @Test
    fun concert_isOneYearApart() {
        val concert1 = Concert(
            name = "",
            dateTime = LocalDateTime.now().plusDays(380),
            duration = Duration.ofMinutes(10),
            bandId = -1,
            city = "",
            country = "",
            place = "",
            concertType = BandItEnums.Concert.Type.Tournament
        )
        val concert2 = Concert(
            name = "",
            dateTime = LocalDateTime.now().plusDays(364),
            duration = Duration.ofHours(100),
            bandId = -1,
            city = "",
            country ="",
            place = "",
            concertType = BandItEnums.Concert.Type.Tournament
        )
        val concert3 = Concert(
            name = "",
            dateTime = LocalDateTime.now().plusYears(1).plusMinutes(1),
            duration = Duration.ofHours(150),
            bandId = -1,
            city = "",
            country = "",
            place = "",
            concertType = BandItEnums.Concert.Type.Tournament
        )
        assertTrue(concert1.isOneYearApart())
        assertTrue(!concert2.isOneYearApart())
        assertTrue(concert3.isOneYearApart())
    }
}