package com.bandit.data.repository

import com.bandit.constant.BandItEnums
import com.bandit.data.db.Database
import com.bandit.data.model.Concert
import java.time.LocalDate
import java.time.LocalTime

class ConcertRepository(database: Database? = null)
    : BaseRepository<Concert>(database, database?.concerts) {
    fun filterConcerts(
        name: String?,
        date: LocalDate?,
        time: LocalTime?,
        city: String?,
        country: String?,
        place: String?,
        type: BandItEnums.Concert.Type?
    ): List<Concert> =
        list
            .asSequence()
            .filter { filter(it.name, name) }
            .filter { filter(it.dateTime.toLocalDate(), date) }
            .filter { filter(it.dateTime.toLocalTime(), time) }
            .filter { filter(it.city, city) }
            .filter { filter(it.country, country) }
            .filter { filter(it.place, place) }
            .filter { filter(it.type, type) }
            .toList()

    override fun reassignId(item: Concert): Concert {
        var newConcert: Concert = item
        while(isIdUsed(newConcert.id)) {
            newConcert = Concert(
                item.name,
                item.dateTime,
                item.city,
                item.country,
                item.place,
                item.type,
                item.bandId
            )
        }
        return newConcert
    }
}