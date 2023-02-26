package com.bandit.data.repository

import com.bandit.constant.BandItEnums
import com.bandit.data.db.Database
import com.bandit.data.model.Event
import java.time.Duration
import java.time.LocalDate
import java.time.LocalTime
import com.bandit.util.FilterUtils

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
    ) = list
        .asSequence()
        .filter { FilterUtils.filter(it.name, name) }
        .filter { FilterUtils.filter(it.dateTime.toLocalDate(), date) }
        .filter { FilterUtils.filter(it.dateTime.toLocalTime(), time) }
        .filter { FilterUtils.filter(it.type, type) }
        .filter { FilterUtils.filter(it.duration, duration) }
        .toList()

    fun getAllEventDates() = list.map { e -> e.dateTime.toLocalDate() }
}
