package com.bandit.mapper

import com.bandit.data.db.dto.BandDto
import com.bandit.data.model.Account
import com.bandit.data.model.Band

object BandMapper {
    fun fromDbEntryToItem(entry: BandDto, members: MutableMap<Account, Boolean>): Band {
        return Band(
            entry.name ?: "",
            entry.creator ?: -1,
            members,
            entry.id
        )
    }

    fun fromItemToDbEntry(item: Band): BandDto {
        val members: MutableMap<Long, Boolean> = mutableMapOf()
        item.members.forEach { (key, value) -> members[key.id] = value }
        return BandDto(
            item.id,
            item.name,
            item.creator
        )
    }
}