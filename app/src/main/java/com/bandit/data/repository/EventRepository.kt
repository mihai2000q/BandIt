package com.bandit.data.repository

import com.bandit.constant.BandItEnums
import com.bandit.data.db.Database
import com.bandit.data.model.Event
import java.time.Duration
import java.time.LocalDate
import java.time.LocalTime

class EventRepository(database: Database? = null)
    : BaseRepository<Event>(database, database?.events) {
    override fun reassignId(item: Event): Event {
        var newEvent: Event = item
        while(isIdUsed(newEvent.id)) {
            newEvent = Event(
                item.name,
                item.dateTime,
                item.duration,
                item.type,
                item.bandId
            )
        }
        return newEvent
    }

    fun filterEvents(
        name: String? = null,
        date: LocalDate? = null,
        time: LocalTime? = null,
        type: BandItEnums.Event.Type? = null,
        duration: Duration? = null
    ): List<Event> =
        list
            .asSequence()
            .filter { filter(it.name, name) }
            .filter { filter(it.dateTime.toLocalDate(), date) }
            .filter { filter(it.dateTime.toLocalTime(), time) }
            .filter { filter(it.type, type) }
            .filter { filter(it.duration, duration) }
            .toList()

}
