package com.bandit.data.model

import com.bandit.data.template.Item
import com.bandit.util.AndroidUtils
import java.time.LocalDateTime

data class Task(
    val checked: Boolean,
    val message: String,
    val bandId: Long,
    val createdOn: LocalDateTime = LocalDateTime.now(),
    override val id: Long = AndroidUtils.generateRandomLong()
) : Item(id), Comparable<Task> {
    override fun compareTo(other: Task): Int {
        return createdOn.compareTo(other.createdOn)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Task

        if (checked != other.checked) return false
        if (message != other.message) return false
        if (bandId != other.bandId) return false
        if (createdOn != other.createdOn) return false

        return true
    }

    override fun hashCode(): Int {
        var result = checked.hashCode()
        result = 31 * result + message.hashCode()
        result = 31 * result + bandId.hashCode()
        result = 31 * result + createdOn.hashCode()
        return result
    }

    companion object {
        val EMPTY = Task(false, "", -1)
    }
}
