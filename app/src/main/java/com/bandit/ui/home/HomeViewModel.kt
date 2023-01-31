package com.bandit.ui.home

import android.content.Context
import android.widget.TableLayout
import android.widget.TableRow
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.bandit.builder.HomeBuilder
import com.bandit.constant.Constants
import com.bandit.di.DILocator
import com.google.android.material.bottomnavigation.BottomNavigationView

class HomeViewModel : ViewModel() {
    private val _builder: HomeBuilder = DILocator.homeBuilder
    private val _elements = MutableLiveData(DILocator.database.homeNavigationElementsMap)
    fun generateHomeElements(
        layout: TableLayout, context:Context,
        bottomNav:BottomNavigationView?
    ) {
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

    companion object {
        const val TAG = Constants.Home.VIEW_MODEL_TAG
    }
}