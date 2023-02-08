package com.bandit.data.db.dto

import com.bandit.data.model.BaseModel

abstract class BaseBandDto(
    override val id: Long,
    open val bandId: Long
) : BaseModel(id)