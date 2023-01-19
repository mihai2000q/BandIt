package com.bandit.data.repository

import com.bandit.constant.BandItEnums
import com.bandit.data.db.Database
import com.bandit.data.model.Concert
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

class ConcertRepository(database: Database? = null) : BaseRepository(database) {
    private val _concerts: MutableList<Concert> = mutableListOf()
    val concerts: List<Concert> get() = _concerts
    init {
        _concerts.addAll(_database?.concerts ?: listOf())
    }
    suspend fun addConcert(concert: Concert) {
        val newConcert = reassignId(concert)
        _database?.addConcert(newConcert)
        _concerts.add(newConcert)
    }
    suspend fun removeConcert(concert: Concert): Boolean {
        _database?.removeConcert(concert)
        if(!_concerts.contains(concert)) return false
        _concerts.remove(concert)
        return true
    }
    suspend fun editConcert(concert: Concert) {
        _database?.editConcert(concert)
        _concerts
            .asSequence()
            .filter { it.id == concert.id }
            .forEach { _concerts[_concerts.indexOf(it)] = concert }
    }
    fun filterConcerts(
        name: String?,
        date: LocalDate?,
        time: LocalTime?,
        city: String?,
        country: String?,
        place: String?,
        type: BandItEnums.Concert.Type?
    ): List<Concert> =
        _concerts
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
    private fun reassignId(concert: Concert): Concert {
        var newConcert: Concert = concert
        while(isIdUsed(newConcert.id)) {
            newConcert = Concert(
                concert.name,
                concert.dateTime,
                concert.city,
                concert.country,
                concert.place,
                concert.type,
                userUid = concert.userUid
            )
        }
        return newConcert
    }
    private fun isIdUsed(id: Int): Boolean {
        return !_concerts.none { it.id == id }
    }
}