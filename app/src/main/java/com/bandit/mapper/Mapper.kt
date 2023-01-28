package com.bandit.mapper

import com.bandit.data.db.dto.BaseDto
import com.bandit.data.model.BaseModel

sealed interface Mapper<T, E>
where
T : BaseModel,
E : BaseDto
{
    fun fromDtoToItem(dto: E): T
    fun fromItemToDto(item: T): E
}