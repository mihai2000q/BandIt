package com.bandit.data.dto

import com.bandit.data.template.Item

data class BandDto(
    override val id: Long = -1,
    val name: String? = null,
    val creator: Long? = null
) : Item(id)