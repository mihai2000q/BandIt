package com.bandit.data.repository

import com.bandit.data.db.Database
import com.bandit.data.model.Album
import com.bandit.data.model.Song
import com.bandit.extension.normalizeWord
import java.time.Duration
import java.time.LocalDate

class AlbumRepository(database: Database? = null)
    : BaseRepository<Album>(database, database?.albums) {
    fun filterAlbums(
        name: String? = null,
        releaseDate: LocalDate? = null,
        label: String? = null,
        duration: Duration? = null
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
                item.releaseDate,
                item.label,
                item.songs
            )
        }
        return newAlbum
    }

    suspend fun addSong(albumId: Long, song: Song) {
        val album = list.first { it.id == albumId }
        album.songs.add(song)
        edit(album)
    }

    suspend fun removeSong(albumId: Long, song: Song) {
        val album = list.first { it.id == albumId }
        if(album.songs.remove(song))
            edit(album)
    }

    fun isThereAnAlbum(name: String) : Long? {
        val filteredList = list.filter { it.name == name.normalizeWord() }
        return if(filteredList.size == 1)
             filteredList.first().id
        else null
    }


}