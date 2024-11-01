package com.bandit.data.dto

import com.bandit.data.template.TemplateAccountDto

data class BandInvitationDto(
    override val id: Long = -1,
    val bandId: Long? = null,
    override val accountId: Long = - 1,
    var accepted: Boolean? = null
) : TemplateAccountDto(id, accountId)