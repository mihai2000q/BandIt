package com.bandit.model

import com.bandit.constant.BandItEnums
import com.bandit.constant.Constants
import com.bandit.data.model.Event
import org.junit.Assert
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.BeforeClass
import org.junit.Test
import org.mockito.Mockito
import java.time.Duration
import java.time.LocalDateTime

class EventTest {
    companion object {
        private val defaultDateAndTime = LocalDateTime.parse("2023-02-05T10:00")
        @BeforeClass
        @JvmStatic
        fun setup() {
            val localDateTime = Mockito.mockStatic(LocalDateTime::class.java, Mockito.CALLS_REAL_METHODS)
            localDateTime.`when`<Any> { LocalDateTime.now() }.thenReturn(defaultDateAndTime)
        }
    }
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
    @Test
    fun event_isOutdated() {
        val event1 = Event(
            name = "",
            dateTime = LocalDateTime.now().minusSeconds(1),
            duration = Duration.ofMinutes(10),
            type = Event.EMPTY.type,
            bandId = -1
        )
        val event2 = Event(
            name = "",
            dateTime = LocalDateTime.now().plusSeconds(1),
            duration = Duration.ofMinutes(10),
            type = Event.EMPTY.type,
            bandId = -1
        )
        Assert.assertTrue(event1.isOutdated())
        Assert.assertTrue(!event2.isOutdated())
    }
    @Test
    fun event_isInTheSameDay() {
        val event1 = Event(
            name = "",
            dateTime = LocalDateTime.now().plusHours(14),
            duration = Duration.ofMinutes(10),
            type = Event.EMPTY.type,
            bandId = -1
        )
        val event2 = Event(
            name = "",
            dateTime = LocalDateTime.now().plusHours(14).minusSeconds(1),
            duration = Duration.ofMinutes(10),
            type = Event.EMPTY.type,
            bandId = -1
        )
        Assert.assertTrue(!event1.isInTheSameDay())
        Assert.assertTrue(event2.isInTheSameDay())
    }
    @Test
    fun event_is24HoursApart() {
        val event1 = Event(
            name = "",
            dateTime =LocalDateTime.now().plusHours(3),
            duration = Duration.ofMinutes(100),
            type = Event.EMPTY.type,
            bandId = -1
        )
        val event2 = Event(
            name = "",
            dateTime = LocalDateTime.now().plusHours(25),
            duration = Duration.ofMinutes(100),
            type = Event.EMPTY.type,
            bandId =  -1
        )
        val event3 = Event(
            name = "",
            dateTime = LocalDateTime.now().plusHours(23).plusMinutes(59),
            duration = Duration.ofMinutes(120),
            type = Event.EMPTY.type,
            bandId = -1
        )
        Assert.assertTrue(event1.is24HoursApart())
        Assert.assertTrue(!event2.is24HoursApart())
        Assert.assertTrue(event3.is24HoursApart())
    }
    @Test
    fun event_is7DaysApart() {
        val event1 = Event(
            name = "",
            dateTime = LocalDateTime.now().plusDays(3),
            duration = Duration.ofHours(3),
            type = Event.EMPTY.type,
            bandId =  -1
        )
        val event2 = Event(
            name = "",
            dateTime = LocalDateTime.now().plusDays(8),
            duration = Duration.ofHours(100),
            type = Event.EMPTY.type,
            bandId = -1
        )
        val event3 = Event(
            name = "",
            dateTime = LocalDateTime.now().plusDays(6).plusHours(23).plusMinutes(59),
            duration = Duration.ofMinutes(98),
            type = Event.EMPTY.type,
            bandId = -1
        )
        Assert.assertTrue(event1.is7DaysApart())
        Assert.assertTrue(!event2.is7DaysApart())
        Assert.assertTrue(event3.is7DaysApart())
    }
    @Test
    fun event_isOneYearApart() {
        val event1 = Event(
            name = "",
            dateTime = LocalDateTime.now().plusDays(380),
            duration = Duration.ofMinutes(10),
            type = Event.EMPTY.type,
            bandId = -1
        )
        val event2 = Event(
            name = "",
            dateTime = LocalDateTime.now().plusDays(364),
            duration = Duration.ofHours(100),
            type = Event.EMPTY.type,
            bandId = -1
        )
        val event3 = Event(
            name = "",
            dateTime = LocalDateTime.now().plusYears(1).plusMinutes(1),
            duration = Duration.ofHours(150),
            type = Event.EMPTY.type,
            bandId = -1
        )
        Assert.assertTrue(event1.isOneYearApart())
        Assert.assertTrue(!event2.isOneYearApart())
        Assert.assertTrue(event3.isOneYearApart())
    }
}