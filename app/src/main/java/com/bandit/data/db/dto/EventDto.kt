package com.bandit.data.db.dto


data class EventDto(
    override val id: Long = -1,
    val name: String? = null,
    val dateTime: String? = null,
    val type: Long? = null,
    override val bandId: Long = -1
) : BaseDto(id, bandId)
