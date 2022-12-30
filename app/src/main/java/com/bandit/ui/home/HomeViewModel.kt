package com.bandit.ui.home

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
import androidx.navigation.NavController
import com.google.android.material.R
import com.google.android.material.button.MaterialButton

class HomeViewModel : ViewModel() {
    private val _elements = MutableLiveData<List<String>>()
    val elements: LiveData<List<String>> get() = _elements
    init {
        _elements.value = listOf("Concerts", "Songs", "Chats", "Schedule", "To do list")
    }

    fun generateHomeElements(layout: TableLayout, context:Context, nav:NavController) {
        _elements.value?.forEach {
            val tableRow: TableRow
            val index = _elements.value!!.indexOf(it)
            if (index % 2 == 0) {
                tableRow = createTableRow("table_row_" + (index + 1), context)
                layout.addView(tableRow)
            } else
                tableRow = layout.findViewWithTag("table_row_$index")

            tableRow.addView(createButton(it, context, nav))
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
    private fun createButton(text: String, context: Context, nav:NavController): Button {
        val button = MaterialButton(
            ContextThemeWrapper(context,
            R.style.Widget_Material3_Button)
        )
        val params = TableRow.LayoutParams()
        params.width = TableRow.LayoutParams.WRAP_CONTENT
        params.height = 400
        params.setMargins(16)
        button.layoutParams = params
        button.text = "Your $text"
        button.gravity = Gravity.CENTER
        //button.setOnClickListener { nav.navigate(R.id) }
        return button
    }
}