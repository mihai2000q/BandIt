package com.bandit.builder

import android.content.Context
import android.widget.Button
import android.widget.TableRow
import com.bandit.helper.BandItEnums
import com.google.android.material.bottomnavigation.BottomNavigationView

interface HomeBuilder {
    fun buildHomeButton(
        context: Context,
        entry: Map.Entry<String, BandItEnums.Home.NavigationType>,
        bottomNav: BottomNavigationView?
    ): Button
    fun buildTableRow(context: Context, tag: String): TableRow
}