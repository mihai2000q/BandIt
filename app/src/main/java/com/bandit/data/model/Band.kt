package com.bandit.data.model

import com.bandit.util.AndroidUtils

data class Band(
    val name: String,
    val members: MutableMap<Account, Boolean>,
    override val id: Long = AndroidUtils.generateRandomLong()
) : BaseModel(id) {
    fun isEmpty(): Boolean {
        return this == EMPTY
    }

    companion object {
        val EMPTY = Band("", mutableMapOf())
    }
}
