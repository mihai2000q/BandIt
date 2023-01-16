package com.bandit.util

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences.Editor
import com.bandit.constant.Constants

object PreferencesUtils {
    fun savePreference(activity: Activity, key: String, value: Any) {
        val editor: Editor = activity
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