package com.bandit.data.mapper

import com.bandit.constant.BandItEnums
import com.bandit.data.dto.EventDto
import com.bandit.data.model.Concert
import com.bandit.data.model.Event
import com.bandit.util.ParserUtils
import java.time.LocalDateTime

object EventMapper : MapperBandItems<Event, EventDto> {
    override fun fromDtoToItem(dto: EventDto): Event {
        return Event(
            name = dto.name ?: "Null",
            dateTime = LocalDateTime.parse(dto.dateTime),
            duration = ParserUtils.parseDurationSeconds(dto.duration),
            type =  this.mapIntToEventType(dto.type?.toInt() ?: 0),
            bandId = dto.bandId,
            id = dto.id
        )
    }

    override fun fromItemToDto(item: Event): EventDto {
        return EventDto(
            id = item.id,
            name = item.name,
            dateTime = item.dateTime.toString(),
            duration = item.duration.seconds,
            type = this.mapEventTypeToInt(item.type)?.toLong(),
            bandId = item.bandId
        )
    }

    fun fromEventToConcert(event: Event): Concert {
        return Concert(
            name = event.name,
            dateTime = event.dateTime,
            duration = event.duration,
            bandId = event.bandId,
            city = null,
            country = null,
            place = null,
            concertType = BandItEnums.Concert.Type.Simple,
            id = event.id
        )
    }

    fun editEventToConcert(event: Event, concert: Concert): Concert {
        return Concert(
            name = event.name,
            dateTime = event.dateTime,
            duration = event.duration,
            bandId = event.bandId,
            city = concert.city,
            country = concert.country,
            place = concert.place,
            concertType = concert.concertType,
            id = event.id
        )
    }

    private fun mapIntToEventType(type: Int): BandItEnums.Event.Type {
        return BandItEnums.Event.Type.values().first { it.ordinal == type }
    }
    private fun mapEventTypeToInt(type: BandItEnums.Event.Type?): Int? {
        return type?.ordinal
    }
}