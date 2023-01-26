package com.bandit.mapper

import com.bandit.constant.BandItEnums
import com.bandit.data.db.dto.AccountDto
import com.bandit.data.model.Account

object AccountMapper {
    fun fromDbEntryToItem(entry: AccountDto): Account {
        return Account(
            entry.name ?: "",
            entry.nickname ?: "",
            this.mapIntToAccountRole(entry.role?.toInt() ?: 0),
            entry.bandId,
            entry.email ?: "",
            entry.id,
            entry.userUid
        )
    }

    fun fromItemToDbEntry(item: Account): AccountDto {
        return AccountDto(
            item.id,
            item.name,
            item.nickname,
            this.mapAccountRoleToInt(item.role).toLong(),
            item.email,
            item.bandId,
            item.userUid ?: ""
        )
    }

    private fun mapIntToAccountRole(type: Int): BandItEnums.Account.Role {
        return BandItEnums.Account.Role.values().first { it.ordinal == type }
    }
    private fun mapAccountRoleToInt(role: BandItEnums.Account.Role): Int {
        return role.ordinal
    }
}