package com.bandit.service.impl

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import com.bandit.constant.Constants
import com.bandit.service.IPreferencesService

class PreferencesService(
    private val activity: Activity
) : IPreferencesService {
    override fun resetAllPreferences() {
        val editor: SharedPreferences.Editor = activity
            .getSharedPreferences(Constants.Preferences.APP_PREFERENCES, Context.MODE_PRIVATE).edit()
        editor.putBoolean(Constants.Preferences.REMEMBER_ME, false)
        editor.apply()
    }
    override fun savePreference(key: String, value: Any) {
        val editor: SharedPreferences.Editor = activity
            .getSharedPreferences(Constants.Preferences.APP_PREFERENCES, Context.MODE_PRIVATE).edit()
        editor.putBoolean(key, value as Boolean)
        editor.apply()
    }
    override fun getBooleanPreference(key: String): Boolean {
        return activity
            .getSharedPreferences(Constants.Preferences.APP_PREFERENCES, Context.MODE_PRIVATE)
            .getBoolean(key, false)
    }
}