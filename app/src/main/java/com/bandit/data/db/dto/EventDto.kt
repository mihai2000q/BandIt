package com.bandit.data.db.dto

import com.bandit.template.TemplateBandDto


data class EventDto(
    override val id: Long = -1,
    val name: String? = null,
    val dateTime: String? = null,
    val duration: Long? = null,
    val type: Long? = null,
    override val bandId: Long = -1
) : TemplateBandDto(id, bandId)
