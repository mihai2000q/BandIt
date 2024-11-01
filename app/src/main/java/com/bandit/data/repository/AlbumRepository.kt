package com.bandit.data.repository

import com.bandit.data.db.Database
import com.bandit.data.model.Album
import com.bandit.data.model.Song
import com.bandit.data.template.TemplateRepository
import com.bandit.util.FilterUtils.filter
import java.time.Duration
import java.time.LocalDate

class AlbumRepository(database: Database? = null)
    : TemplateRepository<Album>(database, database?.albums) {
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

    fun addSong(album: Album, song: Song): Song {
        song.albumId = album.id
        song.albumName = album.name
        album.songs.add(song)
        return song
    }

    fun removeSong(album: Album, song: Song): Song {
        if(!album.songs.remove(song)) return Song.EMPTY
        song.albumId = null
        song.albumName = null
        return song
    }

    fun editSong(album: Album, newSong: Song) {
        val oldSong = album.songs.first { it.id == newSong.id }
        album.songs[album.songs.indexOf(oldSong)] = newSong
    }
}