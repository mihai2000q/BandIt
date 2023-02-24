package com.bandit.service

interface IPreferencesService {
    fun resetAllPreferences()
    fun savePreference(key: String, value: Any)
    fun getBooleanPreference(key: String): Boolean
}