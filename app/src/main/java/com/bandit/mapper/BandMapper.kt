package com.bandit.mapper

import com.bandit.data.db.entry.BandDBEntry
import com.bandit.data.model.Account
import com.bandit.data.model.Band

object BandMapper {
    fun fromDbEntryToItem(entry: BandDBEntry, members: MutableMap<Account, Boolean>): Band {
        return Band(
            entry.name ?: "",
            members,
            entry.id
        )
    }

    fun fromItemToDbEntry(item: Band): BandDBEntry {
        val members: MutableMap<Long, Boolean> = mutableMapOf()
        item.members.forEach { (key, value) -> members[key.id] = value }
        return BandDBEntry(
            item.id,
            item.name
        )
    }
}