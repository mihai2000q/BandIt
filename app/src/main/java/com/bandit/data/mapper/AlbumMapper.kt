package com.bandit.data.mapper

import com.bandit.data.dto.AlbumDto
import com.bandit.data.model.Album
import java.time.LocalDate

object AlbumMapper : MapperB<Album, AlbumDto> {
    override fun fromDtoToItem(dto: AlbumDto): Album {
        return Album(
            name = dto.name ?: "",
            bandId = dto.bandId,
            releaseDate = LocalDate.parse(dto.releaseDate),
            label = dto.label ?: "",
            songs = mutableListOf(),
            id = dto.id
        )
    }

    override fun fromItemToDto(item: Album): AlbumDto {
        return AlbumDto(
            name = item.name,
            bandId = item.bandId,
            releaseDate = item.releaseDate.toString(),
            label = item.label,
            id = item.id
        )
    }
}