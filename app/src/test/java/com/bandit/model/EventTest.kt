package com.bandit.model

import com.bandit.constant.BandItEnums
import com.bandit.constant.Constants
import com.bandit.data.model.Event
import org.junit.Assert
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Test
import java.time.Duration
import java.time.LocalDateTime

class EventTest {
    @Test
    fun event_init() {
        val event = Event(
            name = "new event",
            dateTime = LocalDateTime.of(2023,10,12,8,30),
            type = BandItEnums.Event.Type.Simple,
            duration = Duration.ofMinutes(60),
            bandId = -1
        )
        assertEquals(event.name, "new event")
        assertEquals(event.dateTime, LocalDateTime.parse("2023-10-12T08:30"))
        assertEquals(event.duration, Duration.ofMinutes(60))
        assertEquals(event.type, BandItEnums.Event.Type.Simple)
    }
    @Test
    fun event_init_id() {
        //Repeated tests improvised
        for (i in 1..100) {
            val event = Event.EMPTY
            Assert.assertNotNull(event.id)
            if (event.id < 0 || event.id > Constants.MAX_NR_ITEMS)
                Assert.fail("The Id should be between these boundaries")
        }
    }
    @Test
    fun event_equals() {
        val event1 = Event(
            name = "new event",
            dateTime = LocalDateTime.of(2023,10,12,8,30),
            type = BandItEnums.Event.Type.Simple,
            duration = Duration.ofMinutes(60),
            bandId = -1
        )
        val event2 = Event(
            name = "new event2",
            dateTime = LocalDateTime.of(2023,10,12,8,30),
            type = BandItEnums.Event.Type.Simple,
            duration = Duration.ofMinutes(60),
            bandId = -1
        )
        val event3 = Event(
            name = "new event",
            dateTime = LocalDateTime.of(2023,10,12,8,30),
            type = BandItEnums.Event.Type.Simple,
            duration = Duration.ofMinutes(60),
            bandId = -1
        )
        assertEquals(event1, event3)
        assertNotEquals(event1, event2)
    }
    @Test
    fun event_sort_by_date_time() {
        val outcome = mutableListOf(
            Event(
                name = "event3",
                dateTime = LocalDateTime.of(2023,10,12,8,30),
                type = BandItEnums.Event.Type.Simple,
                duration = Duration.ofMinutes(60),
                bandId = -1
            ),
            Event(
                name = "event1",
                dateTime = LocalDateTime.of(2023,10,11,0,30),
                type = BandItEnums.Event.Type.Simple,
                duration = Duration.ofMinutes(60),
                bandId = -1
            ),
            Event(
                name = "event2",
                dateTime = LocalDateTime.of(2023,10,12,7,30),
                type = BandItEnums.Event.Type.Simple,
                duration = Duration.ofMinutes(60),
                bandId = -1
            )
        )
        outcome.sort()
        val expected = listOf(
            Event(
                name = "event1",
                dateTime = LocalDateTime.of(2023,10,11,0,30),
                type = BandItEnums.Event.Type.Simple,
                duration = Duration.ofMinutes(60),
                bandId = -1
            ),
            Event(
                name = "event2",
                dateTime = LocalDateTime.of(2023,10,12,7,30),
                type = BandItEnums.Event.Type.Simple,
                duration = Duration.ofMinutes(60),
                bandId = -1
            ),
            Event(
                name = "event3",
                dateTime = LocalDateTime.of(2023,10,12,8,30),
                type = BandItEnums.Event.Type.Simple,
                duration = Duration.ofMinutes(60),
                bandId = -1
            )
        )
        assertEquals(outcome, expected)
    }

}