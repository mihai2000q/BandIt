package com.bandit.data.db.dto

import com.bandit.template.TemplateModel

data class BandDto(
    override val id: Long = -1,
    val name: String? = null,
    val creator: Long? = null
) : TemplateModel(id)