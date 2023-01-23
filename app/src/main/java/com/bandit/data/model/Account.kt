package com.bandit.data.model

import com.bandit.constant.BandItEnums
import com.bandit.util.AndroidUtils

data class Account(
    var name: String,
    var nickname: String,
    var role: BandItEnums.Account.Role,
    var bandId: Long?,
    var isSetup: Boolean = false,
    override val id: Long = AndroidUtils.generateRandomLong(),
    val userUid: String? = ""
) : BaseModel(id) {
    fun isEmpty(): Boolean {
        return this == EMPTY
    }
    companion object {
        val EMPTY = Account("", "", BandItEnums.Account.Role.LeadGuitar, -1)
    }
}
