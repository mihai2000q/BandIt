package com.bandit.data.model

import com.bandit.util.AndroidUtils
import java.time.Duration
import java.time.LocalDate

data class Album(
    val name: String,
    val bandId: Long,
    val songs: MutableList<Song> = mutableListOf(),
    val releaseDate: LocalDate? = null,
    val label: String = "",
    val duration: Duration = Duration.ZERO,
    override val id: Long = AndroidUtils.generateRandomLong()
) : BaseModel(id) {

    init {
        songs.forEach { duration.plus(it.duration) }
    }
}