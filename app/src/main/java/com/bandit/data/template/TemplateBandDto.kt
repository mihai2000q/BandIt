package com.bandit.data.template

abstract class TemplateBandDto(
    override val id: Long,
    open val bandId: Long
) : Item(id)