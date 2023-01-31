package com.bandit.mapper

import com.bandit.data.db.dto.SongDto
import com.bandit.data.model.Song
import java.time.Duration
import java.time.LocalDate

object SongMapper : Mapper<Song, SongDto> {
    override fun fromDtoToItem(dto: SongDto): Song {
        return Song(
            dto.name ?: "",
            dto.bandId,
            LocalDate.parse(dto.releaseDate),
            Duration.parse("PT${dto.duration}S"),
            dto.albumName,
            dto.albumId,
            dto.id
        )
    }

    override fun fromItemToDto(item: Song): SongDto {
        return SongDto(
            item.name,
            item.bandId,
            item.releaseDate.toString(),
            item.duration.seconds,
            item.albumName,
            item.albumId,
            item.id
        )
    }
}