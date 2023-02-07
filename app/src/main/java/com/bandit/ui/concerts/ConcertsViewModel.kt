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
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import java.time.Duration
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
    suspend fun addConcert(concert: Concert) = coroutineScope {
        launch { _repository.add(concert) }.join()
        _concerts.value = _repository.list
    }
    suspend fun removeConcert(concert: Concert) = coroutineScope {
        launch { _repository.remove(concert) }.join()
        _concerts.value = _repository.list
    }
    suspend fun editConcert(concert: Concert) = coroutineScope {
        launch { _repository.edit(concert) }.join()
        _concerts.value = _repository.list
    }
    fun filterConcerts(
        name: String?,
        date: LocalDate? = null,
        time: LocalTime? = null,
        duration: Duration? = null,
        city: String? = null,
        country: String? = null,
        place: String? = null,
        type: BandItEnums.Concert.Type? = null
    ) {
        _concerts.value = _repository.filterConcerts(name, date, time, duration, city, country, place, type)
    }

    companion object {
        const val TAG = Constants.Concert.VIEW_MODEL_TAG
    }
}