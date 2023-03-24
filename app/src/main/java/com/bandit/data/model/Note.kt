package com.bandit.data.model

import com.bandit.data.template.Item
import com.bandit.util.AndroidUtils
import java.time.LocalDateTime

data class Note(
    val title: String,
    val content: String,
    val accountId: Long,
    val createdOn: LocalDateTime = LocalDateTime.now(),
    override val id: Long = AndroidUtils.generateRandomLong()
) : Item(id), Comparable<Note> {
    override fun compareTo(other: Note): Int {
        return createdOn.compareTo(other.createdOn)
    }
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Note

        if (title != other.title) return false
        if (content != other.content) return false
        if (accountId != other.accountId) return false
        if (createdOn != other.createdOn) return false

        return true
    }

    override fun hashCode(): Int {
        var result = title.hashCode()
        result = 31 * result + content.hashCode()
        result = 31 * result + accountId.hashCode()
        result = 31 * result + createdOn.hashCode()
        return result
    }

    override fun toString(): String {
        return "Note(id=$id, title='$title', content='$content', accountId=$accountId, createdOn=$createdOn)"
    }

    companion object {
        val EMPTY = Note("", "", -1)
    }
}