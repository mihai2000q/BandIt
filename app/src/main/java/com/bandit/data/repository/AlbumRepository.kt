package com.bandit.data.repository

import com.bandit.data.db.Database
import com.bandit.data.model.Album
import com.bandit.extension.normalizeWord
import java.time.Duration
import java.time.LocalDate

class AlbumRepository(database: Database? = null)
    : BaseRepository<Album>(database, database?.albums) {
    fun filterAlbums(
        name: String?,
        releaseDate: LocalDate?,
        label: String?,
        duration: Duration?
    ): List<Album> =
        list
            .asSequence()
            .filter { filter(it.name, name) }
            .filter { filter(it.releaseDate, releaseDate) }
            .filter { filter(it.label, label) }
            .filter { filter(it.duration, duration) }
            .toList()

    override fun reassignId(item: Album): Album {
        var newAlbum: Album = item
        while(isIdUsed(newAlbum.id)) {
            newAlbum = Album(
                item.name,
                item.bandId,
                item.songs,
                item.releaseDate,
                item.label
            )
        }
        return newAlbum
    }

    fun isThereAnAlbum(name: String) : Long? {
        val filteredList = list.filter { it.name == name.normalizeWord() }
        return if(filteredList.size == 1)
             filteredList.first().id
        else null
    }


}