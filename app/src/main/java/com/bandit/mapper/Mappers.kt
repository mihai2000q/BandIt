package com.bandit.mapper

import com.bandit.constant.BandItEnums

sealed class Mappers {
    object Concert {
        fun mapIntToConcertType(type: Int): BandItEnums.Concert.Type {
            return BandItEnums.Concert.Type.values().first { it.ordinal == type }
        }
        fun mapConcertTypeToInt(type: BandItEnums.Concert.Type): Int {
            return type.ordinal
        }
    }
}