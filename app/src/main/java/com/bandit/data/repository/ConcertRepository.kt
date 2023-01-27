package com.bandit.data.repository

import com.bandit.constant.BandItEnums
import com.bandit.data.db.Database
import com.bandit.data.model.Concert
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

class ConcertRepository(database: Database? = null) : BaseRepository<Concert>(database, database?.concerts) {
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
            .filter { filterString(it.name, name) }
            .filter { filterDate(it.dateTime, date) }
            .filter { filterTime(it.dateTime, time) }
            .filter { filterString(it.city, city) }
            .filter { filterString(it.country, country) }
            .filter { filterString(it.place, place) }
            .filter { filterType(it.type, type) }
            .toList()
    private fun filterDate(dateTime: LocalDateTime, other: LocalDate?) =
        if(other == null)
            true
        else dateTime.toLocalDate().equals(other)
    private fun filterTime(dateTime: LocalDateTime, other: LocalTime?) =
        if(other == null)
            true
        else dateTime.toLocalTime().equals(other)
    private fun filterType(type: BandItEnums.Concert.Type, other: BandItEnums.Concert.Type?) =
        if(other == null)
            true
        else type == other

    private fun filterString(string: String, other: String?) =
        if(other != null) {
            if(other.split(" ").size > 1)
                filterOneString(string, other)
            else
                filterMultipleStrings(string, other)
        } else true
    private fun filterMultipleStrings(string: String, other: String?): Boolean {
        string.split(" ").forEach {
            if(filterOneString(it, other))
                return true
        }
        return false
    }
    private fun filterOneString(string: String, other: String?) =
        string.lowercase().startsWith(other?.lowercase() ?: "")
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