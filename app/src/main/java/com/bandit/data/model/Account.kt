package com.bandit.data.model

import com.bandit.constant.BandItEnums
import com.bandit.util.AndroidUtils

data class Account(
    val firstName: String,
    val lastName: String,
    val nickname: String,
    val role: BandItEnums.Account.Role,
    val bands: List<Band>,
    val isSetup: Boolean = false,
    override val id: Int = AndroidUtils.generateRandomId(),
    val userUid: String? = ""
) : BaseModel(id) {
    val name get() = "$firstName $lastName"
}
