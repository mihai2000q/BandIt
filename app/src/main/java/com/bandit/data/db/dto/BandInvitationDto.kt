package com.bandit.data.db.dto

data class BandInvitationDto(
    override val id: Long = -1,
    val bandId: Long? = null,
    val accountId: Long? = null,
    var accepted: Boolean? = null
) : BaseDto(id)