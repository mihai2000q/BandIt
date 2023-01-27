package com.bandit.data.model

import com.bandit.util.AndroidUtils

data class BandInvitation (
    val band: Band,
    val account: Account,
    var hasAccepted: Boolean,
    override val id: Long = AndroidUtils.generateRandomLong()
) : BaseModel(id) {
    fun isEmpty(): Boolean {
        return this == EMPTY
    }
    companion object {
        val EMPTY = BandInvitation(Band.EMPTY, Account.EMPTY, false)
    }
}