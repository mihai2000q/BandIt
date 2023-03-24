package com.bandit.data.dto

import com.bandit.data.template.TemplateAccountDto

data class NoteDto(
    val title: String? = null,
    val content: String? = null,
    val createdOn: String? = null,
    override val accountId: Long = -1,
    override val id: Long = -1
) : TemplateAccountDto(id, accountId)