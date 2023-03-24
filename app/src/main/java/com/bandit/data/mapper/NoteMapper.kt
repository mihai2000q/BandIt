package com.bandit.data.mapper

import com.bandit.data.dto.NoteDto
import com.bandit.data.model.Note
import java.time.LocalDateTime

object NoteMapper : MapperAccountItems<Note, NoteDto> {
    override fun fromDtoToItem(dto: NoteDto): Note {
        return Note(
            title = dto.title ?: "",
            content = dto.content ?: "",
            createdOn = LocalDateTime.parse(dto.createdOn),
            accountId = dto.accountId,
            id = dto.id
        )
    }

    override fun fromItemToDto(item: Note): NoteDto {
        return NoteDto(
            title = item.title,
            content = item.content,
            createdOn = item.createdOn.toString(),
            accountId = item.accountId,
            id = item.id
        )
    }
}