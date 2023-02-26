package com.bandit.template

abstract class TemplateBandDto(
    override val id: Long,
    open val bandId: Long
) : TemplateModel(id)