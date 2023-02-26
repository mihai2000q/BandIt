package com.bandit.data.model

import com.bandit.template.TemplateModel
import com.bandit.util.AndroidUtils

data class Band(
    val name: String,
    val creator: Long,
    val members: MutableMap<Account, Boolean>,
    override val id: Long = AndroidUtils.generateRandomLong()
) : TemplateModel(id) {
    fun isEmpty(): Boolean {
        return this == EMPTY
    }

    companion object {
        val EMPTY = Band("", -1, mutableMapOf())
    }
}
