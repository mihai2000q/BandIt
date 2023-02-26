package com.bandit.mapper

import com.bandit.template.TemplateAccountDto
import com.bandit.template.TemplateModel

sealed interface MapperA<T, E>
where
T : TemplateModel,
E : TemplateAccountDto
{
    fun fromDtoToItem(dto: E): T
    fun fromItemToDto(item: T): E
}