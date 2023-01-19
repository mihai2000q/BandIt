package com.bandit.extension

object StringExtensions {
    fun String.normalizeWord() =
        this[0].uppercase() + this.substring(1).lowercase()
    fun String.get2Characters() =
        if(this.length == 1) "0$this" else this
}
