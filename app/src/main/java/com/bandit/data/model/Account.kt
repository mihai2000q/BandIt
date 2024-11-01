package com.bandit.data.model

import android.os.Parcelable
import com.bandit.constant.BandItEnums
import com.bandit.data.template.Item
import com.bandit.extension.normalizeWord
import com.bandit.util.AndroidUtils
import kotlinx.parcelize.Parcelize

@Parcelize
data class Account(
    var name: String,
    var nickname: String,
    var role: BandItEnums.Account.Role,
    val email: String,
    var bandId: Long?,
    var bandName: String?,
    override val id: Long = AndroidUtils.generateRandomLong(),
    val userUid: String? = ""
) : Item(id), Comparable<Account>, Parcelable {
    override fun compareTo(other: Account): Int {
        return nickname.compareTo(other.nickname)
    }
    override fun toString(): String {
        return "Account(id=$id, name='$name', nickname='$nickname', role=$role, email='$email', bandId=$bandId, bandName=$bandName, userUid=$userUid)"
    }

    fun printRole() =
        if (this.role.name.normalizeWord() == "Leadguitar")
            "Lead Guitar"
        else if(this.role.name.normalizeWord() == "Rhythmguitar")
            "Rhythm Guitar"
        else
            this.role.name.normalizeWord()

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
