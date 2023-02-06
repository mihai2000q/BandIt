package com.bandit.mapper

import com.bandit.data.db.dto.TaskDto
import com.bandit.data.model.Task
import java.time.LocalDateTime

object TaskMapper : Mapper<Task, TaskDto> {
    override fun fromDtoToItem(dto: TaskDto): Task {
        return Task(
            dto.checked ?: false,
            dto.message ?: "",
            dto.bandId,
            LocalDateTime.parse(dto.createdOn),
            dto.id
        )
    }

    override fun fromItemToDto(item: Task): TaskDto {
        return TaskDto(
            item.id,
            item.bandId,
            item.checked,
            item.message,
            item.createdOn.toString()
        )
    }
}