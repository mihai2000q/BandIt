package com.bandit.data.db.entry

data class AccountDBEntry(
    override val id: Long = -1,
    val name: String? = null,
    val nickname: String? = null,
    val role: Long? = null,
    val bandId: Long? = null,
    val userUid: String? = null
) : BaseEntry(id)