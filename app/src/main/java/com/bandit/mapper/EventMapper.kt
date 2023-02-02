package com.bandit.mapper

import com.bandit.constant.BandItEnums
import com.bandit.data.db.dto.EventDto
import com.bandit.data.model.Event
import java.time.LocalDateTime

object EventMapper : Mapper<Event, EventDto> {
    override fun fromDtoToItem(dto: EventDto): Event {
        return Event(
            dto.name ?: "Null",
            LocalDateTime.parse(dto.dateTime),
            mapIntToEventType(dto.type?.toInt() ?: 0),
            dto.bandId,
            dto.id
        )
    }

    override fun fromItemToDto(item: Event): EventDto {
        return EventDto(
            item.id,
            item.name,
            item.dateTime.toString(),
            mapEventTypeToInt(item.type)?.toLong(),
            item.bandId
        )
    }

    private fun mapIntToEventType(type: Int): BandItEnums.Event.Type {
        return BandItEnums.Event.Type.values().first { it.ordinal == type }
    }
    private fun mapEventTypeToInt(type: BandItEnums.Event.Type?): Int? {
        return type?.ordinal
    }
}