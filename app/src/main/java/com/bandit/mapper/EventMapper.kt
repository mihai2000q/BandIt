package com.bandit.mapper

import com.bandit.constant.BandItEnums
import com.bandit.data.db.dto.EventDto
import com.bandit.data.model.Event
import java.time.Duration
import java.time.LocalDateTime

object EventMapper : Mapper<Event, EventDto> {
    override fun fromDtoToItem(dto: EventDto): Event {
        return Event(
            name = dto.name ?: "Null",
            dateTime = LocalDateTime.parse(dto.dateTime),
            duration = Duration.parse("PT${dto.duration}S"),
            type =  mapIntToEventType(dto.type?.toInt() ?: 0),
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
            type = mapEventTypeToInt(item.type)?.toLong(),
            bandId = item.bandId
        )
    }

    private fun mapIntToEventType(type: Int): BandItEnums.Event.Type {
        return BandItEnums.Event.Type.values().first { it.ordinal == type }
    }
    private fun mapEventTypeToInt(type: BandItEnums.Event.Type?): Int? {
        return type?.ordinal
    }
}