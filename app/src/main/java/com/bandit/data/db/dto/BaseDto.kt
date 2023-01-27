package com.bandit.data.db.dto

import com.bandit.data.model.BaseModel

abstract class BaseDto(override val id: Long) : BaseModel(id)