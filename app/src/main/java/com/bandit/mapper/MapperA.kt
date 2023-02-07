package com.bandit.mapper

import com.bandit.data.db.dto.BaseAccountDto
import com.bandit.data.model.BaseModel

sealed interface MapperA<T, E>
where
T : BaseModel,
E : BaseAccountDto
{
    fun fromDtoToItem(dto: E): T
    fun fromItemToDto(item: T): E
}