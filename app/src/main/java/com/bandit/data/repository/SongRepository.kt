package com.bandit.data.repository

import com.bandit.data.db.Database
import com.bandit.data.model.Song
import java.time.Duration
import java.time.LocalDate

class   SongRepository(database: Database? = null)
    : BaseRepository<Song>(database, database?.songs) {
    fun filterSongs(
        name: String? = null,
        releaseDate: LocalDate? = null,
        albumName: String? = null,
        duration: Duration? = null
    ): List<Song> =
        list
            .asSequence()
            .filter { filter(it.name, name) }
            .filter { filter(it.releaseDate, releaseDate) }
            .filter { filter(it.albumName, albumName) }
            .filter { filter(it.duration, duration) }
            .toList()

    override fun reassignId(item: Song): Song {
        var newSong: Song = item
        while(isIdUsed(newSong.id)) {
            newSong = Song(
                item.name,
                item.bandId,
                item.releaseDate,
                item.duration,
                item.albumName,
                item.albumId,
            )
        }
        return newSong
    }

    fun getSongsWithoutAnAlbum(): List<Song> {
        return list.filter { it.albumId == null }
    }
}