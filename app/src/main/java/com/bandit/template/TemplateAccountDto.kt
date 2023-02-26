package com.bandit.template

abstract class TemplateAccountDto(
    override val id: Long,
    open val accountId: Long
): TemplateModel(id)