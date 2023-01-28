package com.bandit.data.db.dto

import com.bandit.data.model.BaseModel

data class AccountDto(
    override val id: Long = -1,
    val name: String? = null,
    val nickname: String? = null,
    val role: Long? = null,
    val email: String? = null,
    val bandId: Long? = null,
    val userUid: String? = null
) : BaseModel(id)