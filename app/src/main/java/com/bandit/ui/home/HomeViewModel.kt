package com.bandit.ui.home

import android.content.Context
import android.widget.TableLayout
import android.widget.TableRow
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.bandit.builder.HomeBuilder
import com.bandit.helper.DILocator
import com.bandit.helper.NavigationType
import com.google.android.material.bottomnavigation.BottomNavigationView

class HomeViewModel : ViewModel() {
    private val _builder: HomeBuilder
    private val _elements = MutableLiveData<Map<String, NavigationType>>()
    val elements: LiveData<Map<String, NavigationType>> get() = _elements
    init {
        val database = DILocator.getDatabase()
        _elements.value = database.homeNavigationElementsMap
        _builder = DILocator.getHomeBuilder()
    }

    fun generateHomeElements(elements: Map<String, NavigationType>,
                             layout: TableLayout, context:Context,
                             bottomNav:BottomNavigationView?) {
        var index = 0
        elements.forEach {
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