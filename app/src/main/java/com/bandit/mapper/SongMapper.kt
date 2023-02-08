package com.bandit.mapper

import com.bandit.data.db.dto.SongDto
import com.bandit.data.model.Song
import java.time.Duration
import java.time.LocalDate

object SongMapper : MapperB<Song, SongDto> {
    override fun fromDtoToItem(dto: SongDto): Song {
        return Song(
            name = dto.name ?: "",
            bandId = dto.bandId,
            releaseDate = LocalDate.parse(dto.releaseDate),
            duration = Duration.parse("PT${dto.duration}S"),
            albumName = dto.albumName,
            albumId = dto.albumId,
            id = dto.id
        )
    }

    override fun fromItemToDto(item: Song): SongDto {
        return SongDto(
            name = item.name,
            bandId = item.bandId,
            releaseDate = item.releaseDate.toString(),
            duration = item.duration.seconds,
            albumName = item.albumName,
            albumId = item.albumId,
            id = item.id
        )
    }
}