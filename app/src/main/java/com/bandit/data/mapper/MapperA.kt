package com.bandit.data.mapper

import com.bandit.data.template.TemplateAccountDto
import com.bandit.data.template.Item

sealed interface MapperA<T, E>
where
T : Item,
E : TemplateAccountDto
{
    fun fromDtoToItem(dto: E): T
    fun fromItemToDto(item: T): E
}