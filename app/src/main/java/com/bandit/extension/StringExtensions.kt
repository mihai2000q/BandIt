package com.bandit.extension

object StringExtensions {
    fun String.normalizeWord() =
        this[0].uppercase() + this.substring(1).lowercase()
}
