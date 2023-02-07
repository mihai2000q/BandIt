package com.bandit.mapper

import com.bandit.data.db.dto.BaseBandDto
import com.bandit.data.model.BaseModel

sealed interface MapperB<T, E>
where
T : BaseModel,
E : BaseBandDto
{
    fun fromDtoToItem(dto: E): T
    fun fromItemToDto(item: T): E
}