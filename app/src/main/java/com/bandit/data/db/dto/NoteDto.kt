package com.bandit.data.db.dto

import com.bandit.template.TemplateAccountDto

data class NoteDto(
    val title: String? = null,
    val content: String? = null,
    val createdOn: String? = null,
    override val accountId: Long = -1,
    override val id: Long = -1
) : TemplateAccountDto(id, accountId)