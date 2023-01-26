package com.bandit.data.db.dto

data class BandInvitationDto(
    val id: Long? = null,
    val bandId: Long? = null,
    val accountId: Long? = null,
    val accepted: Boolean? = null
)