package com.bandit.data.repository

import com.bandit.constant.BandItEnums
import com.bandit.data.db.Database
import com.bandit.data.model.Concert
import java.time.Duration
import java.time.LocalDate
import java.time.LocalTime
import com.bandit.util.FilterUtils.filter

class ConcertRepository(database: Database? = null)
    : BaseRepository<Concert>(database, database?.concerts) {
    fun filterConcerts(
        name: String? = null,
        date: LocalDate? = null,
        time: LocalTime? = null,
        duration: Duration? = null,
        city: String? = null,
        country: String? = null,
        place: String? = null,
        type: BandItEnums.Concert.Type? = null
    ): List<Concert> =
        list
            .asSequence()
            .filter { filter(it.name, name) }
            .filter { filter(it.dateTime.toLocalDate(), date) }
            .filter { filter(it.dateTime.toLocalTime(), time) }
            .filter { filter(it.duration, duration) }
            .filter { filter(it.city, city) }
            .filter { filter(it.country, country) }
            .filter { filter(it.place, place) }
            .filter { filter(it.concertType, type) }
            .toList()

    override fun reassignId(item: Concert): Concert {
        var newConcert: Concert = item
        while(isIdUsed(newConcert.id)) {
            newConcert = Concert(
                item.name,
                item.dateTime,
                item.duration,
                item.bandId,
                item.city,
                item.country,
                item.place,
                item.concertType
            )
        }
        return newConcert
    }
}