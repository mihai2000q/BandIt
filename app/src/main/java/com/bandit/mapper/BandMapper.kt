package com.bandit.mapper

import com.bandit.data.db.entry.BandDBEntry
import com.bandit.data.model.Band

object BandMapper: Mapper<Band, BandDBEntry> {
    override fun fromDbEntryToItem(entry: BandDBEntry): Band {
        return Band(
            entry.name,
            entry.id
        )
    }

    override fun fromItemToDbEntry(item: Band): BandDBEntry {
        return BandDBEntry(
            item.id,
            item.name
        )
    }
}