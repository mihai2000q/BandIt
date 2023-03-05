package com.bandit.data.model

import com.bandit.data.template.Item
import com.bandit.util.AndroidUtils

data class Band(
    val name: String,
    val creator: Long,
    val members: MutableMap<Account, Boolean>,
    override val id: Long = AndroidUtils.generateRandomLong()
) : Item(id) {
) : Item(id), Comparable<Band> {
    fun isEmpty(): Boolean {
        return this == EMPTY
    }

    override fun compareTo(other: Band): Int {
        return this.name.compareTo(other.name)
    }
    companion object {
        val EMPTY = Band("", -1, mutableMapOf())
    }
}
