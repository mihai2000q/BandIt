package com.bandit.data.db.entry

data class ConcertDBEntry(
    override val id: Long,
    val name: String,
    val dateTime: String,
    val city: String,
    val country: String,
    val place: String,
    val type: Long,
    override val userUid: String
) : BaseEntry(id, userUid)