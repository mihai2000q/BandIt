package com.bandit.data.db.dto

import com.bandit.template.TemplateBandDto

data class AlbumDto(
    val name: String? = null,
    override val bandId: Long = -1,
    val releaseDate: String? = null,
    val label: String? = null,
    override val id: Long = -1
) : TemplateBandDto(id, bandId)