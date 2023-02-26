package com.bandit.data.dto

import com.bandit.data.template.TemplateBandDto

data class AlbumDto(
    val name: String? = null,
    override val bandId: Long = -1,
    val releaseDate: String? = null,
    val label: String? = null,
    override val id: Long = -1
) : TemplateBandDto(id, bandId)