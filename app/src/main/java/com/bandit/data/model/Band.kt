package com.bandit.data.model

import com.bandit.util.AndroidUtils

data class Band(
    val name: String,
    val members: MutableList<Account>,
    override val id: Long = AndroidUtils.generateRandomLong()
) : BaseModel(id)
