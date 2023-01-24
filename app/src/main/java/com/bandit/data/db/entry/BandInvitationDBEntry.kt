package com.bandit.data.db.entry

data class BandInvitationDBEntry(
    val id: Long? = null,
    val bandId: Long? = null,
    val accountId: Long? = null,
    val accepted: Boolean? = null
)