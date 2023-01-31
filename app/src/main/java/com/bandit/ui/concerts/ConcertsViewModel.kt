package com.bandit.ui.concerts

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bandit.constant.BandItEnums
import com.bandit.constant.Constants
import com.bandit.data.model.Concert
import com.bandit.data.repository.ConcertRepository
import com.bandit.di.DILocator
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalTime

class ConcertsViewModel : ViewModel() {
    private val _repository = ConcertRepository(DILocator.database)
    private val _concerts = MutableLiveData(_repository.list)
    val concerts: LiveData<List<Concert>> = _concerts
    val selectedConcert: MutableLiveData<Concert> = MutableLiveData()

    enum class Filter { Name, Date, Time, City, Country, Place, Type }
    private val _filters = MutableLiveData<MutableMap<Filter, String>>()
    val filters: LiveData<MutableMap<Filter, String>> get() = _filters
    init {
        _filters.value = mutableMapOf()
        Filter.values().forEach { _filters.value?.put(it, "") }
    }
    fun addConcert(concert: Concert) {
        viewModelScope.launch {
            launch { _repository.add(concert) }.join()
            _concerts.value = _repository.list
        }
    }
    fun removeConcert(concert: Concert): Boolean {
        var result = false
        viewModelScope.launch {
            launch { result = _repository.remove(concert) }.join()
            _concerts.value = _repository.list
        }
        return result
    }
    fun editConcert(concert: Concert) {
        viewModelScope.launch {
            launch { _repository.edit(concert) }.join()
            _concerts.value = _repository.list
        }
    }
    fun filterConcerts(
        name: String?,
        date: LocalDate? = null,
        time: LocalTime? = null,
        city: String? = null,
        country: String? = null,
        place: String? = null,
        type: BandItEnums.Concert.Type? = null
    ) {
        _concerts.value = _repository.filterConcerts(name, date, time, city, country, place, type)
    }

    companion object {
        const val TAG = Constants.Concert.VIEW_MODEL_TAG
    }
}