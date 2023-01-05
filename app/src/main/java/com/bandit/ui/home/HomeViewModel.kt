package com.bandit.ui.home

import android.content.Context
import android.widget.TableLayout
import android.widget.TableRow
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.bandit.helper.Constants
import com.bandit.data.MockDatabase
import com.bandit.builder.AndroidBuilder
import com.bandit.builder.HomeBuilder
import com.google.android.material.bottomnavigation.BottomNavigationView

class HomeViewModel : ViewModel() {
    private val _builder: HomeBuilder
    private val _elements = MutableLiveData<Map<String, Constants.NavigationType>>()
    val elements: LiveData<Map<String, Constants.NavigationType>> get() = _elements
    init {
        val mockDatabase = MockDatabase()
        _elements.value = mockDatabase.homeNavigationElementsMap
        _builder = AndroidBuilder()
    }

    fun generateHomeElements(layout: TableLayout, context:Context,
                             bottomNav:BottomNavigationView) {
        var index = 0
        _elements.value?.forEach {
            val tableRow: TableRow
            val prefix = "table_row_"
            if (index % 2 == 0) {
                tableRow = _builder.buildTableRow(context, prefix + (index + 1))
                layout.addView(tableRow)
            } else
                tableRow = layout.findViewWithTag(prefix + index)

            index++
            tableRow.addView(_builder.buildHomeButton(context, it, bottomNav))
        }
    }

}