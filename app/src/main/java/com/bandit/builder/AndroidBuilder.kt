package com.bandit.builder

import android.content.Context
import android.graphics.Color
import android.view.ContextThemeWrapper
import android.view.Gravity
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TableRow
import android.widget.TextView
import androidx.core.view.setMargins
import com.bandit.data.model.Concert
import com.bandit.helper.Constants
import com.bandit.helper.NavigationType
import com.bandit.helper.Normalization
import com.google.android.material.R
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.button.MaterialButton
import java.time.LocalDateTime

class AndroidBuilder : HomeBuilder, ConcertBuilder {
    override fun buildHomeButton(
        context: Context,
        entry: Map.Entry<String, NavigationType>,
        bottomNav: BottomNavigationView
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
            NavigationType.Bottom -> button.setOnClickListener {
                bottomNav.selectedItemId = destination
            }
            NavigationType.Drawer -> button.setOnClickListener {
                //TODO: Find a way to either make this work or the navController
                //drawerNav.setCheckedItem(destination)
            }
        }
        return button
    }
    override fun buildConcertTableRow(context: Context, concert: Concert): TableRow {
        val tableRow = buildTableRow(context, "")
        val layout = buildLinearLayout(context, 32, 24)
        when(concert.type) {
            Concert.Type.Simple -> layout.setBackgroundColor(Color.CYAN)
            Concert.Type.Tournament -> layout.setBackgroundColor(Color.RED)
            Concert.Type.Festival -> layout.setBackgroundColor(Color.GREEN)
        }
        val textSize = 16f
        layout.addView(
            buildTextView(
                context,
                concert.name.uppercase(),
                textSize + textSize / 2f,
                16
            )
        )
        if (isConcert7DaysApart(concert))
            layout.addView(
                buildTextView(
                    context,
                    Normalization.normalizeWord(concert.dateTime.dayOfWeek.name),
                    textSize,
                    16
                )
            )
        else
            layout.addView(
                buildTextView(
                    context,
                    "${concert.dateTime.dayOfMonth} " +
                            Normalization.normalizeWord(concert.dateTime.month.name),
                    textSize,
                    16
                )
            )
        layout.addView(
            buildTextView(
                context,
                "${Normalization.normalizeWord(concert.city)}, " +
                    Normalization.normalizeWord(concert.country),
                textSize,
                16
            )
        )
        layout.addView(
            buildTextView(
                context,
                Normalization.normalizeWord(concert.place),
                textSize,
                16
            )
        )
        tableRow.addView(layout)
        return tableRow
    }
    override fun buildTableRow(context: Context, tag: String): TableRow {
        val tableRow = TableRow(context)
        tableRow.layoutParams = TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT
            ,TableRow.LayoutParams.WRAP_CONTENT)
        tableRow.tag = tag
        return tableRow
    }
    private fun buildLinearLayout(context: Context,
                                  marginsVertical: Int, marginsHorizontal: Int): LinearLayout {
        val layout = LinearLayout(context)
        val params = TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT,
            TableRow.LayoutParams.WRAP_CONTENT)
        params.setMargins(marginsVertical, marginsHorizontal,
                            marginsVertical, marginsHorizontal)
        layout.layoutParams = params
        layout.orientation = LinearLayout.VERTICAL
        return layout
    }
    private fun buildTextView(context: Context, text: String,
                              textSize: Float, margins: Int): TextView {
        val textView = TextView(context)
        val params = TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT,
            TableRow.LayoutParams.WRAP_CONTENT)
        params.setMargins(margins)
        textView.layoutParams = params
        textView.text = text
        textView.textSize = textSize
        return textView
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
    private fun isConcert7DaysApart(concert: Concert): Boolean {
        return  LocalDateTime.now().year == concert.dateTime.year &&
                LocalDateTime.now().month == concert.dateTime.month &&
                concert.dateTime.dayOfMonth - LocalDateTime.now().dayOfMonth <= 7
    }
}