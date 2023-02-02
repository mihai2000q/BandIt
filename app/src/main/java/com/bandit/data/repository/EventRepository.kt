package com.bandit.data.repository

import com.bandit.constant.BandItEnums
import com.bandit.data.db.Database
import com.bandit.data.model.Event
import java.time.Duration
import java.time.LocalDateTime

class EventRepository(database: Database? = null)
    : BaseRepository<Event>(database, database?.events) {
    override fun reassignId(item: Event): Event {
        var newEvent: Event = item
        while(isIdUsed(newEvent.id)) {
            newEvent = Event(
                item.name,
                item.dateTime,
                item.type,
                item.bandId
            )
        }
        return newEvent
    }

    fun filterEvents(
        name: String? = null,
        dateTime: LocalDateTime? = null,
        type: BandItEnums.Event.Type? = null,
    ): List<Event> =
        list
            .asSequence()
            .filter { filter(it.name, name) }
            .filter { filter(it.dateTime, dateTime) }
            .filter { filter(it.type, type) }
            .toList()

}
