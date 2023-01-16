package com.bandit.util

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import android.view.View
import android.view.inputmethod.InputMethodManager
import com.bandit.constant.Constants
import kotlin.random.Random

object AndroidUtils {
    fun generateRandomId() = Random.nextInt(Constants.MAX_NR_ITEMS)
    //Context.INPUT_METHOD_SERVICE
    fun hideKeyboard(activity: Activity, inputMethodService: String, view: View) {
        val input = activity.getSystemService(inputMethodService) as InputMethodManager
        input.hideSoftInputFromWindow(view.windowToken, 0)
    }
    fun savePreference(activity: Activity, key: String ,value: Any) {
        val editor: SharedPreferences.Editor = activity
            .getSharedPreferences(Constants.Preferences.APP_PREFERENCES, Context.MODE_PRIVATE).edit()
        editor.putBoolean(key, value as Boolean)
        editor.apply()
    }
    fun getBooleanPreference(activity: Activity, key: String): Boolean {
        return activity
            .getSharedPreferences(Constants.Preferences.APP_PREFERENCES, Context.MODE_PRIVATE)
            .getBoolean(key, false)
    }
}
