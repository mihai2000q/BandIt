package com.bandit.data.db.dto

data class NoteDto(
    val title: String? = null,
    val content: String? = null,
    val createdOn: String? = null,
    override val accountId: Long = -1,
    override val id: Long = -1
) : BaseAccountDto(id, accountId)