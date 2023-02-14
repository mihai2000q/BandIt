package com.bandit.data.db.dto

data class FriendRequestDto(
    override val id: Long = -1,
    override val accountId: Long = -1,
    val friendId: Long = -1
) : BaseAccountDto(id, accountId)