package com.bandit.data.db.dto

import com.bandit.template.TemplateBandDto

data class TaskDto(
    override val id: Long = -1,
    override val bandId: Long = -1,
    val checked: Boolean? = null,
    val message: String? = null,
    val createdOn: String? = null,
) : TemplateBandDto(id, bandId)
