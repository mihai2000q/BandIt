package com.bandit.data.db.dto

data class BandInvitationDto(
    override val id: Long = -1,
    val bandId: Long? = null,
    override val accountId: Long = - 1,
    var accepted: Boolean? = null
) : BaseAccountDto(id, accountId)