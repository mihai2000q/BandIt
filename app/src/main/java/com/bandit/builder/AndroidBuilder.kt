package com.bandit.builder

import android.annotation.SuppressLint
import android.content.Context
import android.view.ContextThemeWrapper
import android.view.Gravity
import android.widget.Button
import android.widget.TableRow
import androidx.core.view.setMargins
import com.bandit.helper.Constants
import com.google.android.material.R
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.button.MaterialButton

class AndroidBuilder : HomeBuilder {
    @SuppressLint("SetTextI18n")
    override fun buildHomeButton(
        context: Context,
        entry: Map.Entry<String, Constants.NavigationType>,
        bottomNav: BottomNavigationView
    ): Button {
        val params = TableRow.LayoutParams()
        params.width = TableRow.LayoutParams.WRAP_CONTENT
        params.height = 400
        val button = buildButton(context, "Your ${entry.key}", params, 16, Gravity.CENTER)
        val destination: Int = com.bandit.R.id::class.java.fields.find {
            it.name.equals("navigation_" +
                    entry.key.lowercase().replace("\\s".toRegex(), "")
            )
        }?.getInt(null) ?: com.bandit.R.id.navigation_home

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
    override fun buildTableRow(context: Context, tag: String): TableRow {
        val tableRow = TableRow(context)
        val params = TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT
            ,TableRow.LayoutParams.WRAP_CONTENT)
        tableRow.layoutParams = params
        tableRow.tag = tag
        return tableRow
    }
    private fun buildButton(context: Context, text: String,
                            params: TableRow.LayoutParams, margins: Int, gravity: Int): Button {
        val button = MaterialButton(
            ContextThemeWrapper(context,
                R.style.Widget_Material3_Button)
        )
        params.setMargins(margins)
        button.layoutParams = params
        button.text = text
        button.gravity = gravity
        return button
    }
}