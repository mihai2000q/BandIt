package com.bandit.data.repository

import com.bandit.data.db.Database
import com.bandit.data.model.Event

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

}
