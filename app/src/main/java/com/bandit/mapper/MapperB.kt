package com.bandit.mapper

import com.bandit.template.TemplateBandDto
import com.bandit.template.TemplateModel

sealed interface MapperB<T, E>
where
T : TemplateModel,
E : TemplateBandDto
{
    fun fromDtoToItem(dto: E): T
    fun fromItemToDto(item: T): E
}