package com.bandit.data.mapper

import com.bandit.data.template.TemplateBandDto
import com.bandit.data.template.Item

/**
 * Mapper interface intended to use on items dependent on a Band object
 */
sealed interface MapperB<T, E>
where
T : Item,
E : TemplateBandDto
{
    /**
     * This method takes as input a DTO and returns an item
     * @param dto
     * @return item
     */
    fun fromDtoToItem(dto: E): T
    /**
     * This method takes as input an item and returns a DTO
     * @param item
     * @return dto
     */
    fun fromItemToDto(item: T): E
}