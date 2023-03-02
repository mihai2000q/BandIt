package com.bandit.ui.concerts

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.bandit.constant.BandItEnums
import com.bandit.constant.Constants
import com.bandit.data.model.Concert
import com.bandit.data.repository.ConcertRepository
import com.bandit.di.DILocator
import com.bandit.util.ParserUtils
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import java.time.Duration
import java.time.LocalDate
import java.time.LocalTime

class ConcertsViewModel : ViewModel() {
    private val _repository = ConcertRepository(DILocator.getDatabase())
    private val _concerts = MutableLiveData(_repository.list)
    val concerts: LiveData<List<Concert>> = _concerts
    val selectedConcert: MutableLiveData<Concert> = MutableLiveData()

    enum class Filter { Name, Date, Time, City, Country, Place, Duration, Type }
    val filters = MutableLiveData<MutableMap<Filter, String>>(mutableMapOf())
    init {
        Filter.values().forEach { filters.value?.put(it, "") }
    }
    suspend fun addConcert(concert: Concert) = coroutineScope {
        launch { _repository.add(concert) }.join()
        this@ConcertsViewModel.refresh()
    }
    suspend fun removeConcert(concert: Concert) = coroutineScope {
        launch { _repository.remove(concert) }.join()
        this@ConcertsViewModel.refresh()
    }
    suspend fun editConcert(concert: Concert) = coroutineScope {
        launch { _repository.edit(concert) }.join()
        this@ConcertsViewModel.refresh()
    }
    private fun refresh() {
        _concerts.value = _repository.list
        with(filters.value!!) {
            if(this.any { it.value == "" })
                this@ConcertsViewModel.filterConcerts(
                    name = this[Filter.Name],
                    date = if(this[Filter.Date].isNullOrEmpty())
                        null
                    else
                        LocalDate.parse(this[Filter.Date]),
                    time = if(this[Filter.Time].isNullOrEmpty())
                        null
                    else
                        LocalTime.parse(this[Filter.Time]),
                    duration = if(this[Filter.Duration].isNullOrEmpty())
                        null
                    else
                        ParserUtils.parseDurationText(this[Filter.Duration]),
                    city = this[Filter.City],
                    country = this[Filter.Country],
                    place = this[Filter.Place]
                )
        }
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

    fun getFiltersOn() = filters.value?.filter { it.value.isNotBlank() }!!.size

    companion object {
        const val TAG = Constants.Concert.VIEW_MODEL_TAG
    }
}