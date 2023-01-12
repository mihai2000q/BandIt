package com.bandit.builder

import android.content.Context
import android.view.ContextThemeWrapper
import android.view.Gravity
import android.widget.Button
import android.widget.TableRow
import androidx.core.view.setMargins
import com.bandit.helper.BandItEnums
import com.google.android.material.R
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.button.MaterialButton

class AndroidBuilder : HomeBuilder {
    override fun buildHomeButton(
        context: Context,
        entry: Map.Entry<String, BandItEnums.Home.NavigationType>,
        bottomNav: BottomNavigationView?
    ): Button {
        val params = TableRow.LayoutParams()
        params.width = TableRow.LayoutParams.WRAP_CONTENT
        params.height = 400
        val button = buildButton(
            context, "Your ${entry.key}", params, 16, Gravity.CENTER, 15f)
        val destination: Int = com.bandit.R.id::class.java.fields.find {
            it.name.equals("navigation_" +
                    entry.key.lowercase().replace("\\s".toRegex(), "")
            )
        }?.getInt(null) ?: com.bandit.R.id.navigation_home

        when(entry.value) {
            BandItEnums.Home.NavigationType.Bottom -> button.setOnClickListener {
                bottomNav?.selectedItemId = destination
            }
            BandItEnums.Home.NavigationType.Drawer -> button.setOnClickListener {
                //TODO: Find a way to either make this work or the navController
                //drawerNav.setCheckedItem(destination)
            }
        }
        return button
    }
    override fun buildTableRow(context: Context, tag: String): TableRow {
        val tableRow = TableRow(context)
        tableRow.layoutParams = TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT
            ,TableRow.LayoutParams.WRAP_CONTENT)
        tableRow.tag = tag
        return tableRow
    }
    private fun buildButton(context: Context, text: String,
                            params: TableRow.LayoutParams,
                            margins: Int, gravity: Int, textSize: Float): Button {
        val button = MaterialButton(
            ContextThemeWrapper(context, R.style.Widget_Material3_Button)
        )
        params.setMargins(margins)
        button.layoutParams = params
        button.text = text
        button.gravity = gravity
        button.textSize = textSize
        return button
    }
}