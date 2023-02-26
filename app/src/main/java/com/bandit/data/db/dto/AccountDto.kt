package com.bandit.data.db.dto

import com.bandit.template.TemplateModel

data class AccountDto(
    override val id: Long = -1,
    val name: String? = null,
    val nickname: String? = null,
    val role: Long? = null,
    val email: String? = null,
    val bandId: Long? = null,
    val bandName: String? = null,
    val userUid: String? = null
) : TemplateModel(id)