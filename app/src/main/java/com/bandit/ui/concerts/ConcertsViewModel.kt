package com.bandit.ui.concerts

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.bandit.data.model.Concert
import com.bandit.data.repository.ConcertRepository
import com.bandit.helper.DILocator

class ConcertsViewModel : ViewModel() {
    private val _repository = ConcertRepository(DILocator.getDatabase())
    private val _concerts = MutableLiveData(_repository.concerts)
    val concerts: LiveData<List<Concert>> get() = _concerts
    val selectedConcert: MutableLiveData<Concert> = MutableLiveData()

    enum class Filter { Name, City, Country }
    private val _filters = MutableLiveData<MutableMap<Filter, String>>()
    val filters: LiveData<MutableMap<Filter, String>> get() = _filters
    init {
        _filters.value = mutableMapOf()
        Filter.values().forEach { _filters.value?.put(it, "") }
    }
    fun addConcert(concert: Concert) {
        _repository.addConcert(concert)
        _concerts.value = _repository.concerts
    }
    fun removeConcert(concert: Concert): Boolean {
        val result = _repository.removeConcert(concert)
        _concerts.value = _repository.concerts
        return result
    }
    fun editConcert(concert: Concert) {
        _repository.editConcert(concert)
        _concerts.value = _repository.concerts
    }
    fun filterConcerts(name: String?, city: String?, country: String?) {
        _concerts.value = _repository.filterConcerts(name, city, country)
    }
}