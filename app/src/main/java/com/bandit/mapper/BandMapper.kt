package com.bandit.mapper

import com.bandit.data.db.dto.BandDto
import com.bandit.data.model.Account
import com.bandit.data.model.Band

object BandMapper {
    fun fromDbEntryToItem(dto: BandDto, members: MutableMap<Account, Boolean>): Band {
        return Band(
            dto.name ?: "",
            dto.creator ?: -1,
            members,
            dto.id
        )
    }

    fun fromItemToDbEntry(item: Band): BandDto {
        return BandDto(
            item.id,
            item.name,
            item.creator
        )
    }
}