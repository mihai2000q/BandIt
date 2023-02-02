package com.bandit.repository

import com.bandit.constant.BandItEnums
import com.bandit.data.model.Event
import com.bandit.data.repository.EventRepository
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test
import java.time.Duration
import java.time.LocalDateTime

class EventRepositoryTest : BaseRepositoryTest<Event>() {
    private lateinit var eventRepository: EventRepository
    @Before
    fun setup() {
        eventRepository = EventRepository()
    }
    override fun import_data() {
        runBlocking {
            eventRepository.add(
                Event(
                    name = "Weekly Training Session",
                    dateTime = LocalDateTime.of(2023,10,12,8,30),
                    type = BandItEnums.Event.Type.Training,
                    bandId = -1,
                    duration = Duration.ofHours(2)
                )
            )
            eventRepository.add(
                Event(
                    name = "Weekly Training Session",
                    dateTime = LocalDateTime.of(2023,11,12,8,30),
                    type = BandItEnums.Event.Type.Training,
                    bandId = -1,
                    duration = Duration.ofHours(2)
                )
            )
            eventRepository.add(
                Event(
                    name = "Meeting for composing",
                    dateTime = LocalDateTime.of(2023,10,13,8,30),
                    type = BandItEnums.Event.Type.Simple,
                    bandId = -1,
                    duration = Duration.ofHours(3)
                )
            )
            eventRepository.add(
                Event(
                    name = "Rock Fest",
                    dateTime = LocalDateTime.of(2023,6,10,7,30),
                    type = BandItEnums.Event.Type.Concert,
                    bandId = -1,
                    duration = Duration.ofHours(1)
                )
            )
            eventRepository.add(
                Event(
                    name = "Session for album naming",
                    dateTime = LocalDateTime.of(2023,12,12,8,0),
                    type = BandItEnums.Event.Type.Simple,
                    bandId = -1,
                    duration = Duration.ofHours(1)
                )
            )
            eventRepository.add(
                Event(
                    name = "Rock fest Training",
                    dateTime = LocalDateTime.of(2023,5,10,8,30),
                    type = BandItEnums.Event.Type.Training,
                    bandId = -1,
                    duration = Duration.ofHours(1)
                )
            )
        }
    }
    @Test
    fun event_repository_add() {
        runBlocking {
            eventRepository.add(
                Event(
                    name = "Weekly Training Session",
                    dateTime = LocalDateTime.of(2023,10,10,8,30),
                    type = BandItEnums.Event.Type.Training,
                    bandId = -1,
                    duration = Duration.ofSeconds(90)
                )
            )
        }
        assert_event(
            eventRepository,
            name = "Weekly Training Session",
            dateTime = LocalDateTime.parse("2023-10-10T08:30"),
            type = BandItEnums.Event.Type.Training,
            duration = Duration.ofSeconds(90)
        )
    }
    @Test
    fun event_repository() {
        repository_remove(eventRepository)
    }
    @Test
    fun event_repository_edit() {
        import_data()
        var eventToEdit = eventRepository.list[0]
        val newEvent = Event(
            name = "new event",
            dateTime = LocalDateTime.parse("2022-10-10T10:00"),
            type = BandItEnums.Event.Type.Simple,
            bandId = -1,
            duration = Duration.ofSeconds(90)
        )
        //before
        assert_event(
            eventRepository,
            name = "Weekly Training Session",
            dateTime = LocalDateTime.of(2023,10,12,8,30),
            type = BandItEnums.Event.Type.Training,
            duration = Duration.ofHours(2)
        )
        eventToEdit = Event(
            name = newEvent.name,
            dateTime = newEvent.dateTime,
            type = newEvent.type,
            bandId = newEvent.bandId,
            duration = newEvent.duration,
            id = eventToEdit.id
        )
        runBlocking { eventRepository.edit(eventToEdit) }
        assert_event(
            eventRepository,
            name = "new event",
            dateTime = LocalDateTime.parse("2022-10-10T10:00"),
            type = BandItEnums.Event.Type.Simple,
            duration = Duration.ofSeconds(90)
        )
    }
    @Test
    fun event_repository_different_ids() {
        different_ids(eventRepository, Event.EMPTY)
    }
    @Test
    fun event_repository_filter() {
        import_data()
        val outcome = eventRepository.filterEvents(
            name = "Weekly Training Session",
            type = BandItEnums.Event.Type.Training,
            duration = Duration.ofHours(2)
        )
        val expected = listOf(
            Event(
                name = "Weekly Training Session",
                dateTime = LocalDateTime.of(2023,10,12,8,30),
                type = BandItEnums.Event.Type.Training,
                bandId = -1,
                duration = Duration.ofHours(2)
            ),
            Event(
                name = "Weekly Training Session",
                dateTime = LocalDateTime.of(2023,11,12,8,30),
                type = BandItEnums.Event.Type.Training,
                bandId = -1,
                duration = Duration.ofHours(2)
            )
        )
        assertEquals(outcome.size, 2)
        assertEquals(outcome, expected)
    }
    private fun assert_event(
        repository: EventRepository,
        name: String,
        dateTime: LocalDateTime,
        duration: Duration,
        type: BandItEnums.Event.Type
    ) {
        assertNotNull(repository.list[0])
        assertEquals(repository.list[0].name, name)
        assertEquals(repository.list[0].dateTime, dateTime)
        assertEquals(repository.list[0].duration, duration)
        assertEquals(repository.list[0].type, type)
    }
}