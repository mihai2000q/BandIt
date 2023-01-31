package com.bandit.builder

import android.app.Activity
import android.content.Context
import android.widget.Button
import android.widget.TableRow
import com.bandit.constant.BandItEnums
import com.google.android.material.bottomnavigation.BottomNavigationView

interface HomeBuilder {
    fun buildHomeButton(
        activity: Activity,
        context: Context,
        entry: Map.Entry<String, BandItEnums.Home.NavigationType>,
        bottomNav: BottomNavigationView?
    ): Button
    fun buildTableRow(context: Context, tag: String): TableRow
}