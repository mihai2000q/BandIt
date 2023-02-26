package com.bandit.data.mapper

import com.bandit.constant.BandItEnums
import com.bandit.data.dto.AccountDto
import com.bandit.data.model.Account

object AccountMapper {
    fun fromDtoToItem(dto: AccountDto): Account {
        return Account(
            name = dto.name ?: "",
            nickname = dto.nickname ?: "",
            role = mapIntToAccountRole(dto.role?.toInt() ?: 0),
            email = dto.email ?: "",
            bandId = dto.bandId,
            bandName = dto.bandName,
            id = dto.id,
            userUid = dto.userUid
        )
    }

    fun fromItemToDto(item: Account): AccountDto {
        return AccountDto(
            id = item.id,
            name = item.name,
            nickname = item.nickname,
            role = mapAccountRoleToInt(item.role).toLong(),
            email = item.email,
            bandId = item.bandId,
            userUid = item.userUid ?: "",
            bandName = item.bandName
        )
    }

    private fun mapIntToAccountRole(type: Int): BandItEnums.Account.Role {
        return BandItEnums.Account.Role.values().first { it.ordinal == type }
    }
    private fun mapAccountRoleToInt(role: BandItEnums.Account.Role): Int {
        return role.ordinal
    }
}