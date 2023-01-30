package com.bandit.mapper

import com.bandit.data.db.dto.AlbumDto
import com.bandit.data.model.Album
import java.time.LocalDate

object AlbumMapper : Mapper<Album, AlbumDto> {
    override fun fromDtoToItem(dto: AlbumDto): Album {
        return Album(
            dto.name ?: "",
            dto.bandId,
            mutableListOf(),
            LocalDate.parse(dto.releaseDate),
            dto.label ?: "",
            dto.id
        )
    }

    override fun fromItemToDto(item: Album): AlbumDto {
        return AlbumDto(
            item.name,
            item.bandId,
            item.releaseDate.toString(),
            item.label,
            item.id
        )
    }
}