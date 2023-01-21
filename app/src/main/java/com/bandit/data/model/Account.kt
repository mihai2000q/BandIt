package com.bandit.data.model

import com.bandit.constant.BandItEnums
import com.bandit.util.AndroidUtils

data class Account(
    val name: String,
    val nickname: String,
    val role: BandItEnums.Account.Role,
    val isSetup: Boolean = false,
    override val id: Long = AndroidUtils.generateRandomLong(),
    val userUid: String? = ""
) : BaseModel(id) {
    val bands: MutableList<Band> = mutableListOf()
}
