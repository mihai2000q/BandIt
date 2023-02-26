package com.bandit.data.mapper

import com.bandit.data.template.TemplateBandDto
import com.bandit.data.template.Item

sealed interface MapperB<T, E>
where
T : Item,
E : TemplateBandDto
{
    fun fromDtoToItem(dto: E): T
    fun fromItemToDto(item: T): E
}