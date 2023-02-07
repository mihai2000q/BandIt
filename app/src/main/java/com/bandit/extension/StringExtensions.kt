package com.bandit.extension

fun String.normalizeWord() = if(this.isEmpty()) this else
    this[0].uppercase() + this.substring(1).lowercase()

fun String.get2Characters() = if(this.isEmpty()) this else
    if(this.length == 1) "0$this" else this