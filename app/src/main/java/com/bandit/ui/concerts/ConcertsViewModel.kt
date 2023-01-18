package com.bandit.ui.concerts

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bandit.data.model.Concert
import com.bandit.data.repository.ConcertRepository
import com.bandit.di.DILocator
import kotlinx.coroutines.launch

class ConcertsViewModel : ViewModel() {
    private val _repository = ConcertRepository(DILocator.database)
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
        viewModelScope.launch {
            launch { _repository.addConcert(concert) }.join()
            _concerts.value = _repository.concerts
        }
    }
    fun removeConcert(concert: Concert): Boolean {
        var result = false
        viewModelScope.launch {
            launch { result = _repository.removeConcert(concert) }.join()
            _concerts.value = _repository.concerts
        }
        return result
    }
    fun editConcert(concert: Concert) {
        viewModelScope.launch {
            launch { _repository.editConcert(concert) }.join()
            _concerts.value = _repository.concerts
        }
    }
    fun filterConcerts(name: String?, city: String?, country: String?) {
        _concerts.value = _repository.filterConcerts(name, city, country)
    }
}