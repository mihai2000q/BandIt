package com.bandit.ui.concerts

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.bandit.data.Database
import com.bandit.data.model.Concert
import com.bandit.helper.DILocator

class ConcertsViewModel : ViewModel() {
    private val database: Database = DILocator.getDatabase()
    private val _concerts = MutableLiveData<List<Concert>>()
    val concerts: LiveData<List<Concert>> get() = _concerts
    val selectedConcert: MutableLiveData<Concert> = MutableLiveData()

    enum class Filter { Name, City, Country }
    private val _filters = MutableLiveData<MutableMap<Filter, String>>()
    val filters: LiveData<MutableMap<Filter, String>> get() = _filters
    init {
        _concerts.value = database.concerts
        _filters.value = mutableMapOf()
        Filter.values().forEach { _filters.value?.put(it, "") }
    }
    fun addConcert(concert: Concert) {
        database.addConcert(concert)
        _concerts.value = database.concerts
    }
    fun removeConcert(concert: Concert): Boolean {
        val result = database.removeConcert(concert)
        _concerts.value = database.concerts
        return result
    }
    fun editConcert(concert: Concert) {
        database.editConcert(concert)
        _concerts.value = database.concerts
    }
    fun filterConcerts(name: String?, city: String?, country: String?) {
        _concerts.value = database.concerts
            .filter { filterOne(it.name, name) }
            .filter { filterOne(it.city, city) }
            .filter { filterOne(it.country, country) }
    }
    private fun filterOne(string: String, other: String?): Boolean {
        string.split(" ").forEach {
            if(it.lowercase().startsWith(other?.lowercase() ?: ""))
                return true
        }
        return false
    }
}