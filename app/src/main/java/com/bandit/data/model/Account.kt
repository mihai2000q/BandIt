package com.bandit.data.model

import com.bandit.constant.BandItEnums
import com.bandit.util.AndroidUtils

data class Account(
    var name: String,
    var nickname: String,
    var role: BandItEnums.Account.Role,
    var bandId: Long?,
    val email: String,
    override val id: Long = AndroidUtils.generateRandomLong(),
    val userUid: String? = ""
) : BaseModel(id), Comparable<Account> {
    override fun compareTo(other: Account): Int {
        return name.compareTo(other.name)
    }
    fun isEmpty(): Boolean {
        return this == EMPTY
    }
    companion object {
        val EMPTY = Account("", "", BandItEnums.Account.Role.LeadGuitar, -1, "")
    }
}
