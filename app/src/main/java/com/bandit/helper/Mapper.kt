package com.bandit.helper

sealed class Mapper {
    object Concert {
        fun mapIntToConcertType(type: Int): BandItEnums.Concert.Type {
            return BandItEnums.Concert.Type.values().first { it.ordinal == type }
        }
        fun mapConcertTypeToInt(type: BandItEnums.Concert.Type): Int {
            return type.ordinal
        }
    }
}