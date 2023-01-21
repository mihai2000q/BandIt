package com.bandit.data.db.entry

data class BandDBEntry(
    override val id: Long,
    val name: String
) : BaseEntry(id) {
    val members: Array<String> = arrayOf()
}