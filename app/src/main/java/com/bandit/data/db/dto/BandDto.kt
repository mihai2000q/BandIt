package com.bandit.data.db.dto

data class BandDto(
    override val id: Long = -1,
    val name: String? = null,
    val creator: Long? = null
) : BaseDto(id)