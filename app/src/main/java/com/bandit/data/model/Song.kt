package com.bandit.data.model

import com.bandit.util.AndroidUtils
import java.time.Duration
import java.time.LocalDate

data class Song(
    val name: String,
    val bandId: Long,
    val releaseDate: LocalDate? = null,
    val albumName: String? = null,
    val albumId: Long? = null,
    val duration: Duration = Duration.ZERO,
    override val id: Long = AndroidUtils.generateRandomLong()
) : BaseModel(id)