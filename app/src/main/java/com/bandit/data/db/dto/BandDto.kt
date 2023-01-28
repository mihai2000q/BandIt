package com.bandit.data.db.dto

import com.bandit.data.model.BaseModel

data class BandDto(
    override val id: Long = -1,
    val name: String? = null,
    val creator: Long? = null
) : BaseModel(id)