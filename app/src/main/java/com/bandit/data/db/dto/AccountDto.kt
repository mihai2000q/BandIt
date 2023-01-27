package com.bandit.data.db.dto

data class AccountDto(
    override val id: Long = -1,
    val name: String? = null,
    val nickname: String? = null,
    val role: Long? = null,
    val email: String? = null,
    val bandId: Long? = null,
    val userUid: String? = null
) : BaseDto(id)