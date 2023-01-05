package com.bandit.helper

sealed class Normalization {
    companion object {
        fun normalizeWord(string: String): String {
            return string[0].uppercase() + string.substring(1).lowercase()
        }
    }
}
