package com.bandit.ui.home

import android.annotation.SuppressLint
import android.content.Context
import android.view.ContextThemeWrapper
import android.view.Gravity
import android.widget.Button
import android.widget.TableLayout
import android.widget.TableRow
import androidx.core.view.setMargins
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.bandit.R
import com.bandit.helper.Constants
import com.bandit.data.MockDatabase
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.button.MaterialButton

class HomeViewModel : ViewModel() {
    private val _elements = MutableLiveData<Map<String, Constants.NavigationType>>()
    val elements: LiveData<Map<String, Constants.NavigationType>> get() = _elements
    init {
        val mockDatabase = MockDatabase()
        _elements.value = mockDatabase.homeNavigationElementsMap
    }

    fun generateHomeElements(layout: TableLayout, context:Context,
                             bottomNav:BottomNavigationView) {
        var index = 0
        _elements.value?.forEach {
            val tableRow: TableRow
            val prefix = "table_row_"
            if (index % 2 == 0) {
                tableRow = createTableRow(prefix + (index + 1), context)
                layout.addView(tableRow)
            } else
                tableRow = layout.findViewWithTag(prefix + index)

            index++
            tableRow.addView(createButton(it, context, bottomNav))
        }
    }
    private fun createTableRow(tag: String, context: Context): TableRow {
        val tableRow = TableRow(context)
        val params = TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT
            ,TableRow.LayoutParams.WRAP_CONTENT)
        tableRow.layoutParams = params
        tableRow.tag = tag
        return tableRow
    }
    @SuppressLint("SetTextI18n")
    private fun createButton(entry: Map.Entry<String, Constants.NavigationType>,
                             context: Context,
                             bottomNav:BottomNavigationView): Button {
        val button = MaterialButton(
            ContextThemeWrapper(context,
            com.google.android.material.R.style.Widget_Material3_Button)
        )
        val params = TableRow.LayoutParams()
        params.width = TableRow.LayoutParams.WRAP_CONTENT
        params.height = 400
        params.setMargins(16)
        button.layoutParams = params
        button.text = "Your ${entry.key}"
        button.gravity = Gravity.CENTER

        val destination: Int = R.id::class.java.fields.find {
            it.name.equals("navigation_" +
                    entry.key.lowercase().replace("\\s".toRegex(), "")
            )
        }?.getInt(null) ?: R.id.navigation_home

        when(entry.value) {
            Constants.NavigationType.Bottom -> button.setOnClickListener {
                bottomNav.selectedItemId = destination
            }
            Constants.NavigationType.Drawer -> button.setOnClickListener {
                //TODO: Find a way to either make this work or the navController
                //drawerNav.setCheckedItem(destination)
            }
        }
        return button
    }
}