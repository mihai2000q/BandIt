package com.bandit.data.model

import com.bandit.constant.BandItEnums
import com.bandit.data.template.Item
import com.bandit.util.AndroidUtils

data class Account(
    var name: String,
    var nickname: String,
    var role: BandItEnums.Account.Role,
    val email: String,
    var bandId: Long?,
    var bandName: String?,
    override val id: Long = AndroidUtils.generateRandomLong(),
    val userUid: String? = ""
) : Item(id), Comparable<Account> {
    override fun compareTo(other: Account): Int {
        return name.compareTo(other.name)
    }
    override fun toString(): String {
        return "Account(id=$id, name='$name', nickname='$nickname', role=$role, email='$email', bandId=$bandId, bandName=$bandName, userUid=$userUid)"
    }

    fun isEmpty(): Boolean {
        return this == EMPTY
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Account

        if (name != other.name) return false
        if (nickname != other.nickname) return false
        if (role != other.role) return false
        if (email != other.email) return false
        if (bandId != other.bandId) return false
        if (bandName != other.bandName) return false

        return true
    }

    override fun hashCode(): Int {
        var result = name.hashCode()
        result = 31 * result + nickname.hashCode()
        result = 31 * result + role.hashCode()
        result = 31 * result + email.hashCode()
        result = 31 * result + (bandId?.hashCode() ?: 0)
        result = 31 * result + (bandName?.hashCode() ?: 0)
        return result
    }

    companion object {
        val EMPTY = Account("", "", BandItEnums.Account.Role.LeadGuitar, "", -1, "")
    }
}
