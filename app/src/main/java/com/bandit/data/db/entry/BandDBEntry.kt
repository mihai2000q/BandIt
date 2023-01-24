package com.bandit.data.db.entry

data class BandDBEntry(
    override val id: Long = -1,
    val name: String? = null
) : BaseEntry(id)