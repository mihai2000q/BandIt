package com.bandit.data.mapper

import com.bandit.data.dto.BandDto
import com.bandit.data.model.Account
import com.bandit.data.model.Band

object BandMapper {
    fun fromDtoToItem(dto: BandDto, members: MutableMap<Account, Boolean>): Band {
        return Band(
            dto.name ?: "",
            dto.creator ?: -1,
            members,
            dto.id
        )
    }

    fun fromItemToDto(item: Band): BandDto {
        return BandDto(
            item.id,
            item.name,
            item.creator
        )
    }
}