package com.bandit.mapper

sealed interface Mapper<T, E> {
    fun fromDbEntryToItem(entry: E): T
    fun fromItemToDbEntry(item: T): E
}