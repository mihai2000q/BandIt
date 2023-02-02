package com.bandit.mapper

import com.bandit.constant.BandItEnums
import com.bandit.data.db.dto.ConcertDto
import com.bandit.data.model.Concert
import java.time.LocalDateTime

object ConcertMapper : Mapper<Concert, ConcertDto> {
    override fun fromDtoToItem(dto: ConcertDto): Concert {
        return Concert(
            dto.name ?: "Null",
            LocalDateTime.parse(dto.dateTime),
            dto.bandId,
            dto.city,
            dto.country,
            dto.place,
            mapIntToConcertType(dto.type?.toInt() ?: 0),
            dto.id
        )
    }

    override fun fromItemToDto(item: Concert): ConcertDto {
        return ConcertDto(
            item.id,
            item.name,
            item.dateTime.toString(),
            item.city,
            item.country,
            item.place,
            mapConcertTypeToInt(item.concertType)?.toLong(),
            item.bandId
        )
    }

    private fun mapIntToConcertType(type: Int): BandItEnums.Concert.Type {
        return BandItEnums.Concert.Type.values().first { it.ordinal == type }
    }
    private fun mapConcertTypeToInt(type: BandItEnums.Concert.Type?): Int? {
        return type?.ordinal
    }
}