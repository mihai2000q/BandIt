package com.bandit.data.db.dto

data class ConcertDto(
    override val id: Long,
    val name: String,
    val dateTime: String,
    val city: String,
    val country: String,
    val place: String,
    val type: Long,
    val userUid: String
) : BaseDto(id)