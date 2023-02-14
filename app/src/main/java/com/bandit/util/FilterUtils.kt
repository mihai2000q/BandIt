package com.bandit.util

import java.util.*

object FilterUtils {
    fun <E> filter(obj: E, other: E) =
        when(obj) {
            is String -> filterString(obj, other as String?)
            else -> filterObjects(obj, other)
        }
    private fun filterString(string: String, other: String?) =
        if(!other.isNullOrEmpty()) {
            if(other.split(" ").size > 1)
                filterOneString(string, other)
            else
                filterMultipleStrings(string, other)
        } else true
    private fun <E> filterObjects(obj: E, other: E) =
        if(other == null || obj == null)
            true
        else Objects.equals(obj, other)
    private fun filterMultipleStrings(string: String, other: String?): Boolean {
        string.split(" ").forEach {
            if(filterOneString(it, other))
                return true
        }
        return false
    }
    private fun filterOneString(string: String, other: String?) =
        string.lowercase().startsWith(other?.lowercase() ?: "")
}