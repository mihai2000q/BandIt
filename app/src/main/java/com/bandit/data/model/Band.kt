package com.bandit.data.model

import com.bandit.util.AndroidUtils

data class Band(
    val name: String,
    val members: List<Account>,
    override val id: Int = AndroidUtils.generateRandomId()
) : BaseModel(id) {

}
