package com.bandit.data.db.dto

import com.bandit.data.model.BaseModel

abstract class BaseAccountDto(
    override val id: Long,
    open val accountId: Long
): BaseModel(id)