package com.bandit.mapper

import com.bandit.constant.BandItEnums
import com.bandit.data.db.entry.ConcertDBEntry
import com.bandit.data.model.Concert
import java.time.LocalDateTime

sealed class Mappers {
    object Concert {
        fun fromConcertToDBEntry(concert: com.bandit.data.model.Concert): ConcertDBEntry {
            return ConcertDBEntry(
                concert.id.toLong(),
                concert.name,
                concert.dateTime.toString(),
                concert.city,
                concert.country,
                concert.place,
                mapConcertTypeToInt(concert.type).toLong()
            )
        }
        fun fromDBEntryToConcert(concertDBEntry: ConcertDBEntry): com.bandit.data.model.Concert {
            return Concert(
                concertDBEntry.name,
                LocalDateTime.parse(concertDBEntry.dateTime),
                concertDBEntry.city,
                concertDBEntry.country,
                concertDBEntry.place,
                mapIntToConcertType(concertDBEntry.type.toInt()),
                concertDBEntry.id.toInt()
            )
        }
        private fun mapIntToConcertType(type: Int): BandItEnums.Concert.Type {
            return BandItEnums.Concert.Type.values().first { it.ordinal == type }
        }
        private fun mapConcertTypeToInt(type: BandItEnums.Concert.Type): Int {
            return type.ordinal
        }
    }
}