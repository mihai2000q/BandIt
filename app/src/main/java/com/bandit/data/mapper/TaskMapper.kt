package com.bandit.data.mapper

import com.bandit.data.dto.TaskDto
import com.bandit.data.model.Task
import java.time.LocalDateTime

object TaskMapper : MapperBandItems<Task, TaskDto> {
    override fun fromDtoToItem(dto: TaskDto): Task {
        return Task(
            checked = dto.checked ?: false,
            message = dto.message ?: "",
            bandId = dto.bandId,
            createdOn = LocalDateTime.parse(dto.createdOn),
            id = dto.id
        )
    }

    override fun fromItemToDto(item: Task): TaskDto {
        return TaskDto(
            id = item.id,
            bandId = item.bandId,
            checked = item.checked,
            message = item.message,
            createdOn = item.createdOn.toString()
        )
    }
}