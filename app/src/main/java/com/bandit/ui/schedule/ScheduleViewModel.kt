package com.bandit.ui.schedule

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.bandit.constant.Constants
import com.bandit.data.model.Event
import com.bandit.data.repository.EventRepository
import com.bandit.di.DILocator
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import java.time.LocalDate

class ScheduleViewModel : ViewModel() {
    private val _repository = EventRepository(DILocator.getDatabase())
    private val _events = MutableLiveData(_repository.list)
    val events: LiveData<List<Event>> = _events
    val selectedEvent = MutableLiveData<Event>()
    val calendarMode = MutableLiveData(false)
    val currentDate = MutableLiveData(LocalDate.now())
    val filterName = MutableLiveData("")

    suspend fun addEvent(event: Event) = coroutineScope {
        launch { _repository.add(event) }.join()
        this@ScheduleViewModel.refresh()
    }

     suspend fun removeEvent(event: Event) = coroutineScope {
         launch { _repository.remove(event) }.join()
         this@ScheduleViewModel.refresh()
    }

    suspend fun editEvent(event: Event) = coroutineScope {
        launch { _repository.edit(event) }.join()
        this@ScheduleViewModel.refresh()
    }

    fun filterEvents(
        name: String? = null,
        date: LocalDate? = null
    ) {
        _events.value = _repository.filterEvents(name = name, date = date)
    }

    fun removeFilters() {
        this.filterEvents()
    }

    fun getDates() = _repository.getAllEventDates()

    private fun refresh() {
        _events.value = _repository.list
        if(calendarMode.value == true)
            this.filterEvents(date = currentDate.value)
        if(!filterName.value.isNullOrBlank())
            this.filterEvents(name = filterName.value)
    }

    companion object {
        const val TAG = Constants.Schedule.VIEW_MODEL_TAG
    }
}