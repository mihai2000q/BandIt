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
    var closeAddDialog: (() -> Unit)? = null
    init {
        _concerts.value = database.concerts
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
}