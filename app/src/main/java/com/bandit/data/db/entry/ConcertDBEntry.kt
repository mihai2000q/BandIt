package com.bandit.data.db.entry

data class ConcertDBEntry(
    val id: Int,
    val name: String,
    val dateTime: String,
    val city: String,
    val country: String,
    val place: String,
    val type: Int
)