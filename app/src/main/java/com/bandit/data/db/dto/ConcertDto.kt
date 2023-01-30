package com.bandit.data.db.dto

data class ConcertDto(
    override val id: Long = -1,
    val name: String? = null,
    val dateTime: String? = null,
    val city: String? = null,
    val country: String? = null,
    val place: String? = null,
    val type: Long? = null,
    override val bandId: Long = -1
) : BaseDto(id, bandId)