package com.bandit.data.db.entry

data class AccountDBEntry(
    override val id: Long,
    val name: String,
    val nickname: String,
    val role: Long,
    val bandId: Long?,
    val isSetup: Boolean,
    val userUid: String
) : BaseEntry(id)