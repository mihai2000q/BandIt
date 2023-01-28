package com.bandit.data.db.dto

import com.bandit.data.model.BaseModel

data class BandInvitationDto(
    override val id: Long = -1,
    val bandId: Long? = null,
    val accountId: Long? = null,
    var accepted: Boolean? = null
) : BaseModel(id)