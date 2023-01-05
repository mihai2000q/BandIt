package com.bandit.ui.concerts

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.bandit.data.MockDatabase
import com.bandit.data.model.Concert
import com.bandit.helper.DILocator

class ConcertsViewModel : ViewModel() {
    private val _concerts = MutableLiveData<List<Concert>>()
    val concerts: LiveData<List<Concert>> get() = _concerts
    init {
        val mockDatabase = DILocator.getDatabase()
        _concerts.value = mockDatabase.concerts
    }
    fun generateConcertsElements() {
        
    }
}