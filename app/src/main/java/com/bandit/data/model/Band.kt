package com.bandit.data.model

import com.bandit.util.AndroidUtils

data class Band(
    val name: String,
    override val id: Long = AndroidUtils.generateRandomLong()
) : BaseModel(id) {
    val members: MutableList<Account> = mutableListOf()

}
