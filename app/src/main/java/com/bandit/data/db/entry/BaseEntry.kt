package com.bandit.data.db.entry

import com.bandit.data.model.BaseModel

abstract class BaseEntry(override val id: Long) : BaseModel(id)