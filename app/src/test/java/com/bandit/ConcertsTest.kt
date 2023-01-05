package com.bandit

import com.bandit.data.model.Concert
import com.bandit.helper.Constants
import org.junit.Test

import org.junit.Assert.*
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ConcertsTest {
    @Test
    fun concert_init() {
        //using date and time
        val concert = Concert(
            "Concert1",
            LocalDateTime.of(2020,10,25,20,30),
            "Los Angeles",
            "United States",
            "Big Arena"
        )
        assertEquals(concert.name, "Concert1")
        assertEquals(concert.dateTime, LocalDateTime.parse("2020-10-25T20:30"))
        assertEquals(concert.city, "Los Angeles")
        assertEquals(concert.country, "United States")
        assertEquals(concert.place, "Big Arena")
    }
    @Test
    fun concert_init_id() {
        //Repeated tests improvised
        for (i in 1..10) {
            val concert = Concert.getEmpty()
            assertNotNull(concert.id)
            if (concert.id < 0 || concert.id > Constants.INT_MAX)
                fail("The Id should be between these boundaries")
        }
    }
}