package com.bandit.data.db.dto

data class SongDto(
    val name: String? = null,
    override val bandId: Long = -1,
    val releaseDate: String? = null,
    val duration: Long? = null,
    val albumName: String? = null,
    val albumId: Long? = null,
    override val id: Long = -1
) : BaseDto(id, bandId)