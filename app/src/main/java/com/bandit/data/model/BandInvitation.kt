package com.bandit.data.model

import com.bandit.template.TemplateModel
import com.bandit.util.AndroidUtils

data class BandInvitation (
    val band: Band,
    val account: Account,
    var hasAccepted: Boolean,
    override val id: Long = AndroidUtils.generateRandomLong()
) : TemplateModel(id), Comparable<BandInvitation> {
    override fun compareTo(other: BandInvitation): Int {
        return band.name.compareTo(other.band.name)
    }

    fun isEmpty(): Boolean {
        return this == EMPTY
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as BandInvitation

        if (band != other.band) return false
        if (account != other.account) return false
        if (hasAccepted != other.hasAccepted) return false

        return true
    }

    override fun hashCode(): Int {
        var result = band.hashCode()
        result = 31 * result + account.hashCode()
        result = 31 * result + hasAccepted.hashCode()
        return result
    }

    companion object {
        val EMPTY = BandInvitation(Band.EMPTY, Account.EMPTY, false)
    }
}