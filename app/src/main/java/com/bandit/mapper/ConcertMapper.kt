package com.bandit.mapper

import com.bandit.constant.BandItEnums
import com.bandit.data.db.entry.ConcertDBEntry
import com.bandit.data.model.Concert
import java.time.LocalDateTime

object ConcertMapper : Mapper<Concert, ConcertDBEntry> {
    override fun fromDbEntryToItem(entry: ConcertDBEntry): Concert {
        return Concert(
            entry.name,
            LocalDateTime.parse(entry.dateTime),
            entry.city,
            entry.country,
            entry.place,
            mapIntToConcertType(entry.type.toInt()),
            entry.id,
            entry.userUid
        )
    }

    override fun fromItemToDbEntry(item: Concert): ConcertDBEntry {
        return ConcertDBEntry(
            item.id,
            item.name,
            item.dateTime.toString(),
            item.city,
            item.country,
            item.place,
            mapConcertTypeToInt(item.type).toLong(),
            item.userUid ?: ""
        )
    }

    private fun mapIntToConcertType(type: Int): BandItEnums.Concert.Type {
        return BandItEnums.Concert.Type.values().first { it.ordinal == type }
    }
    private fun mapConcertTypeToInt(type: BandItEnums.Concert.Type): Int {
        return type.ordinal
    }
}