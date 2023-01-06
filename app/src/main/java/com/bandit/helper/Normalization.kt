package com.bandit.helper

object Normalization {
    fun normalizeWord(string: String) =
        string[0].uppercase() + string.substring(1).lowercase()
}
