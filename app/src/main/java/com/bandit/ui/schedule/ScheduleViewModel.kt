package com.bandit.ui.schedule

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bandit.constant.Constants
import com.bandit.data.model.Event
import com.bandit.data.repository.EventRepository
import com.bandit.di.DILocator
import kotlinx.coroutines.launch

class ScheduleViewModel : ViewModel() {
    private val _repository = EventRepository(DILocator.database)
    private val _events = MutableLiveData(_repository.list)
    val events: LiveData<List<Event>> = _events
    val selectedEvent = MutableLiveData<Event>()
    val calendarMode = MutableLiveData(true)

    fun addEvent(event: Event) {
        viewModelScope.launch {
            launch { _repository.add(event) }.join()
            _events.value = _repository.list
        }
    }

    fun removeEvent(event: Event): Boolean {
        var result = false
        viewModelScope.launch {
            launch { result = _repository.remove(event) }.join()
            _events.value = _repository.list
        }
        return result
    }

    fun filterEvents(
        name: String?
    ) {
        _events.value = _repository.filterEvents(name)
    }

    companion object {
        const val TAG = Constants.Schedule.VIEW_MODEL_TAG
    }
}