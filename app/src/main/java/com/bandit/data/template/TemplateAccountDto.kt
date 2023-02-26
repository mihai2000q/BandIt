package com.bandit.data.template

abstract class TemplateAccountDto(
    override val id: Long,
    open val accountId: Long
): Item(id)