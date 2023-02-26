package com.bandit.data.mapper

import com.bandit.constant.BandItEnums
import com.bandit.data.dto.ConcertDto
import com.bandit.data.model.Concert
import com.bandit.data.model.Event
import java.time.Duration
import java.time.LocalDateTime

object ConcertMapper : MapperB<Concert, ConcertDto> {
    override fun fromDtoToItem(dto: ConcertDto): Concert {
        return Concert(
            name = dto.name ?: "Null",
            dateTime = LocalDateTime.parse(dto.dateTime),
            duration = Duration.parse("PT${dto.duration}S"),
            bandId = dto.bandId,
            city = dto.city,
            country = dto.country,
            place = dto.place,
            concertType =  mapIntToConcertType(dto.type?.toInt() ?: 0),
            id = dto.id
        )
    }

    override fun fromItemToDto(item: Concert): ConcertDto {
        return ConcertDto(
            id = item.id,
            name = item.name,
            dateTime = item.dateTime.toString(),
            duration = item.duration.seconds,
            city = item.city,
            country = item.country,
            place = item.place,
            type = mapConcertTypeToInt(item.concertType)?.toLong(),
            bandId = item.bandId
        )
    }

    fun fromConcertToEvent(concert: Concert): Event {
        return Event(
            concert.name,
            concert.dateTime,
            concert.duration,
            concert.type,
            concert.bandId,
            concert.id
        )
    }

    private fun mapIntToConcertType(type: Int): BandItEnums.Concert.Type {
        return BandItEnums.Concert.Type.values().first { it.ordinal == type }
    }
    private fun mapConcertTypeToInt(type: BandItEnums.Concert.Type?): Int? {
        return type?.ordinal
    }
}