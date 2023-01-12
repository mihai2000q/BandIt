package com.bandit.data.repository

import com.bandit.data.db.Database
import com.bandit.data.model.Concert

class ConcertRepository(database: Database? = null) : BaseRepository(database) {
    private val _concerts: MutableList<Concert> = mutableListOf()
    val concerts: List<Concert> get() = _concerts
    init {
        _concerts.addAll(_database?.concerts ?: listOf())
    }
    fun addConcert(concert: Concert) {
        val newConcert = reassignId(concert)
        _database?.addConcert(newConcert)
        _concerts.add(newConcert)
    }
    fun removeConcert(concert: Concert): Boolean {
        _database?.removeConcert(concert)
        if(!_concerts.contains(concert)) return false
        _concerts.remove(concert)
        return true
    }
    fun editConcert(concert: Concert) {
        _database?.editConcert(concert)
        _concerts
            .asSequence()
            .filter { it.id == concert.id }
            .forEach { _concerts[_concerts.indexOf(it)] = concert }
    }
    fun filterConcerts(name: String?, city: String?, country: String?): List<Concert> {
        return _concerts
            .asSequence()
            .filter { filter(it.name, name) }
            .filter { filter(it.city, city) }
            .filter { filter(it.country, country) }
            .toList()
    }
    private fun filterOne(string: String, other: String?): Boolean {
        string.split(" ").forEach {
            if(it.lowercase().startsWith(other?.lowercase() ?: ""))
                return true
        }
        return false
    }
    private fun filterMultiple(string: String, other: String?): Boolean {
        return string.lowercase().startsWith(other?.lowercase() ?: "")
    }
    private fun filter(string: String, other: String?): Boolean {
        return if(other != null) {
            if(other.split(" ").size > 1)
                filterMultiple(string, other)
            else
                filterOne(string, other)
        } else true
    }
    private fun reassignId(concert: Concert): Concert {
        var newConcert: Concert = concert
        while(isIdUsed(newConcert.id)) {
            newConcert = Concert(
                concert.name,
                concert.dateTime,
                concert.city,
                concert.country,
                concert.place,
                concert.type
            )
        }
        return newConcert
    }
    private fun isIdUsed(id: Int): Boolean {
        return !_concerts.none { it.id == id }
    }
}