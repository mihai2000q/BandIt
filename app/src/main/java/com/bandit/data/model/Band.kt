package com.bandit.data.model

import com.bandit.data.template.Item
import com.bandit.util.AndroidUtils

data class Band(
    val name: String,
    val creator: Long,
    val members: MutableMap<Account, Boolean>,
    override val id: Long = AndroidUtils.generateRandomLong()
) : Item(id), Comparable<Band> {
    fun isEmpty(): Boolean {
        return this == EMPTY
    }

    override fun compareTo(other: Band): Int {
        return this.name.compareTo(other.name)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Band

        if (name != other.name) return false
        if (creator != other.creator) return false
        if (members != other.members) return false

        return true
    }

    override fun hashCode(): Int {
        var result = name.hashCode()
        result = 31 * result + creator.hashCode()
        result = 31 * result + members.hashCode()
        return result
    }

    override fun toString(): String {
        return "Band(id=$id, name='$name', creator=$creator, members=$members)"
    }

    companion object {
        val EMPTY = Band("", -1, mutableMapOf())
    }
}
