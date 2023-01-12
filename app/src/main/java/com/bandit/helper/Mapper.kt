package com.bandit.helper

sealed class Mapper {
    object Concert {
        private val mapTypes = mapOf(
            1 to com.bandit.data.model.Concert.Type.Simple,
            2 to com.bandit.data.model.Concert.Type.Tournament,
            3 to com.bandit.data.model.Concert.Type.Festival
        )
        fun mapIntToConcertType(type: Int): com.bandit.data.model.Concert.Type {
            return mapTypes[type] ?: com.bandit.data.model.Concert.Type.Simple
        }
    }
}