package com.bandit.ui.concerts

import android.content.Context
import android.widget.TableLayout
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.bandit.builder.ConcertBuilder
import com.bandit.data.model.Concert
import com.bandit.helper.DILocator

class ConcertsViewModel : ViewModel() {
    private val _builder: ConcertBuilder = DILocator.getConcertBuilder()
    private val _concerts = MutableLiveData<List<Concert>>()
    val concerts: LiveData<List<Concert>> get() = _concerts
    init {
        val database = DILocator.getDatabase()
        _concerts.value = database.concerts
    }
    fun generateConcertsElements(context: Context, concerts: List<Concert>, layout: TableLayout) {
        concerts.forEach { layout.addView(_builder.buildConcertTableRow(context, it)) }
    }
}