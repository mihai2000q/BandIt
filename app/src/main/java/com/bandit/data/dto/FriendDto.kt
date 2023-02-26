package com.bandit.data.dto

import com.bandit.data.template.TemplateAccountDto

data class FriendDto(
    override val id: Long = -1,
    override val accountId: Long = -1,
    val friendId: Long = -1
) : TemplateAccountDto(id, accountId)