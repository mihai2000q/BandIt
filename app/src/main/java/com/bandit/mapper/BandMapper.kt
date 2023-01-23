package com.bandit.mapper

import com.bandit.data.db.entry.BandDBEntry
import com.bandit.data.model.Account
import com.bandit.data.model.Band

object BandMapper {
    fun fromDbEntryToItem(entry: BandDBEntry, members: List<Account>): Band {
        return Band(
            entry.name,
            members as MutableList<Account>,
            entry.id
        )
    }

    fun fromItemToDbEntry(item: Band): BandDBEntry {
        val memberIds = mutableListOf<Long>()
        item.members.forEach { memberIds.add(it.id) }
        return BandDBEntry(
            item.id,
            item.name,
            memberIds.toTypedArray()
        )
    }
}