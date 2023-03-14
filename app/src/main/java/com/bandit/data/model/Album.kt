package com.bandit.data.model

import com.bandit.data.template.Item
import com.bandit.util.AndroidUtils
import java.time.Duration
import java.time.LocalDate

data class Album(
    val name: String,
    val bandId: Long,
    val releaseDate: LocalDate,
    val label: String = "",
    val songs: MutableList<Song> = mutableListOf(),
    override val id: Long = AndroidUtils.generateRandomLong()
) : Item(id), Comparable<Album> {
    val duration: Duration get() {
        var d = Duration.ZERO
        songs.forEach { d = d.plus(it.duration) }
        return d
    }

    override fun compareTo(other: Album): Int {
        return if(releaseDate.compareTo(other.releaseDate) == 0)
            this.name.compareTo(other.name)
        else
            this.releaseDate.compareTo(other.releaseDate)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Album

        if (name != other.name) return false
        if (bandId != other.bandId) return false
        if (releaseDate != other.releaseDate) return false
        if (label != other.label) return false

        return true
    }

    override fun hashCode(): Int {
        var result = name.hashCode()
        result = 31 * result + bandId.hashCode()
        result = 31 * result + releaseDate.hashCode()
        result = 31 * result + label.hashCode()
        return result
    }

    override fun toString(): String {
        return "Album(id=$id, name='$name', bandId=$bandId, releaseDate=$releaseDate, label='$label', duration=$duration)"
    }

    fun isEmpty() = this == EMPTY

    companion object {
        val EMPTY = Album("", -1, LocalDate.now())
    }
}