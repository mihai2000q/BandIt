package com.bandit.component

import android.content.Context
import android.widget.CalendarView
import java.util.*

class CustomCalendarView(context: Context) : CalendarView(context) {
    fun setMonthDateColor(date: Date?) {
        for (i in 0 until childCount) {
            val weekView = getChildAt(i)
        }
    }
}