package com.bandit.data.model

import com.bandit.template.TemplateModel
import com.bandit.util.AndroidUtils
import java.time.Duration
import java.time.LocalDate

data class Song(
    val name: String,
    val bandId: Long,
    val releaseDate: LocalDate,
    val duration: Duration = Duration.ZERO,
    var albumName: String? = null,
    var albumId: Long? = null,
    override val id: Long = AndroidUtils.generateRandomLong()
) : TemplateModel(id), Comparable<Song> {
    override fun compareTo(other: Song): Int {
        return if(this.releaseDate.compareTo(other.releaseDate) == 0)
            this.name.compareTo(other.name)
        else
            this.releaseDate.compareTo(other.releaseDate)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Song

        if (name != other.name) return false
        if (bandId != other.bandId) return false
        if (releaseDate != other.releaseDate) return false
        if (albumName != other.albumName) return false
        if (albumId != other.albumId) return false
        if (duration != other.duration) return false

        return true
    }

    override fun hashCode(): Int {
        var result = name.hashCode()
        result = 31 * result + bandId.hashCode()
        result = 31 * result + (releaseDate.hashCode())
        result = 31 * result + (albumName?.hashCode() ?: 0)
        result = 31 * result + (albumId?.hashCode() ?: 0)
        result = 31 * result + duration.hashCode()
        return result
    }

    override fun toString(): String {
        return "Song(id=$id, name='$name', bandId=$bandId, releaseDate=$releaseDate, albumName=$albumName, albumId=$albumId, duration=$duration)"
    }

    companion object {
        val EMPTY = Song("", -1, LocalDate.now())
    }
}