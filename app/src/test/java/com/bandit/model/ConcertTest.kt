package com.bandit.model

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
            -1,
            "Los Angeles",
            "United States",
            "Big Arena",
            BandItEnums.Concert.Type.Tournament,
        )
        assertEquals(concert.name, "Concert1")
        assertEquals(concert.dateTime, LocalDateTime.parse("2020-10-25T20:30"))
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
            "Concert",
            LocalDateTime.of(2020,10,25,20,30),
            -1,
            "Los Angeles",
            "USA",
            "Big Arena",
            BandItEnums.Concert.Type.Tournament
        )
        val concert2 = Concert(
            "Concert",
            LocalDateTime.of(2020,10,25,20,30),
            -1,
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
                -1,
                "",
                "",
                "",
                BandItEnums.Concert.Type.Tournament
            ),
            Concert(
                "Concert2",
                LocalDateTime.of(2021,10,25,20,30),
                -1,
                "",
                "",
                "",
                BandItEnums.Concert.Type.Tournament
            ),
            Concert(
                "Concert3",
                LocalDateTime.of(2023,10,25,20,30),
                -1,
                "",
                "",
                "",
                BandItEnums.Concert.Type.Tournament
            ),
            Concert(
                "Concert4",
                LocalDateTime.of(2020,1,23,20,30),
                -1,
                "",
                "",
                "",
                BandItEnums.Concert.Type.Tournament
            ),
            Concert(
                "Concert5",
                LocalDateTime.of(2020,10,25,20,29),
                -1,
                "",
                "",
                "",
                BandItEnums.Concert.Type.Tournament
            ),
            Concert(
                "Concert6",
                LocalDateTime.of(2020,10,25,19,29),
                -1,
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
                -1,
                "",
                "",
                "",
                BandItEnums.Concert.Type.Tournament
            ),
            Concert(
                "Concert6",
                LocalDateTime.of(2020,10,25,19,29),
                -1,
                "",
                "",
                "",
                BandItEnums.Concert.Type.Tournament
            ),
            Concert(
                "Concert5",
                LocalDateTime.of(2020,10,25,20,29),
                -1,
                "",
                "",
                "",
                BandItEnums.Concert.Type.Tournament
            ),
            Concert(
                "Concert1",
                LocalDateTime.of(2020,10,25,20,30),
                -1,
                "",
                "",
                "",
                BandItEnums.Concert.Type.Tournament
            ),
            Concert(
                "Concert2",
                LocalDateTime.of(2021,10,25,20,30),
                -1,
                "",
                "",
                "",
                BandItEnums.Concert.Type.Tournament
            ),
            Concert(
                "Concert3",
                LocalDateTime.of(2023,10,25,20,30),
                -1,
                "",
                "",
                "",
                BandItEnums.Concert.Type.Tournament
            )
        )
        assertEquals(outcome, expected)
    }
    @Test
    fun concert_isOutdated() {
        val concert1 = Concert(
            "",
            LocalDateTime.now().minusSeconds(1),
            -1,
            "",
            "",
            "",
            BandItEnums.Concert.Type.Tournament
        )
        val concert2 = Concert(
            "",
            LocalDateTime.now().plusSeconds(1),
            -1,
            "",
            "",
            "",
            BandItEnums.Concert.Type.Tournament
        )
        assertTrue(concert1.isOutdated())
        assertTrue(!concert2.isOutdated())
    }
    @Test
    fun concert_is24HoursApart() {
        val concert1 = Concert(
            "",
            LocalDateTime.now().plusHours(3),
            -1,
            "",
            "",
            "",
            BandItEnums.Concert.Type.Tournament
        )
        val concert2 = Concert(
            "",
            LocalDateTime.now().plusHours(25),
            -1,
            "",
            "",
            "",
            BandItEnums.Concert.Type.Tournament
        )
        val concert3 = Concert(
            "",
            LocalDateTime.now().plusHours(23).plusMinutes(59),
            -1,
            "",
            "",
            "",
            BandItEnums.Concert.Type.Tournament
        )
        assertTrue(concert1.is24HoursApart())
        assertTrue(!concert2.is24HoursApart())
        assertTrue(concert3.is24HoursApart())
    }
    @Test
    fun concert_is7DaysApart() {
        val concert1 = Concert(
            "",
            LocalDateTime.now().plusDays(3),
            -1,
            "",
            "",
            "",
            BandItEnums.Concert.Type.Tournament
        )
        val concert2 = Concert(
            "",
            LocalDateTime.now().plusDays(8),
            -1,
            "",
            "",
            "",
            BandItEnums.Concert.Type.Tournament
        )
        val concert3 = Concert(
            "",
            LocalDateTime.now().plusDays(6).plusHours(23).plusMinutes(59),
            -1,
            "",
            "",
            "",
            BandItEnums.Concert.Type.Tournament
        )
        assertTrue(concert1.is7DaysApart())
        assertTrue(!concert2.is7DaysApart())
        assertTrue(concert3.is7DaysApart())
    }
    @Test
    fun concert_isOneYearApart() {
        val concert1 = Concert(
            "",
            LocalDateTime.now().plusDays(380),
            -1,
            "",
            "",
            "",
            BandItEnums.Concert.Type.Tournament
        )
        val concert2 = Concert(
            "",
            LocalDateTime.now().plusDays(364),
            -1,
            "",
            "",
            "",
            BandItEnums.Concert.Type.Tournament
        )
        val concert3 = Concert(
            "",
            LocalDateTime.now().plusYears(1).plusMinutes(1),
            -1,
            "",
            "",
            "",
            BandItEnums.Concert.Type.Tournament
        )
        assertTrue(concert1.isOneYearApart())
        assertTrue(!concert2.isOneYearApart())
        assertTrue(concert3.isOneYearApart())
    }
}