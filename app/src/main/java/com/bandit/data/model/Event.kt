package com.bandit.data.model

import com.bandit.constant.BandItEnums
import com.bandit.extension.normalizeWord
import com.bandit.data.template.Item
import com.bandit.util.AndroidUtils
import java.time.Duration
import java.time.LocalDateTime

open class Event(
    open val name: String,
    open val dateTime: LocalDateTime,
    open val duration: Duration,
    val type: BandItEnums.Event.Type,
    open val bandId: Long,
    override val id: Long = AndroidUtils.generateRandomLong()
) : Item(id), Comparable<Event> {
    override fun compareTo(other: Event): Int {
        return this.dateTime.compareTo(other.dateTime)
    }

    fun isOutdated(): Boolean {
        return LocalDateTime.now().isAfter(this.dateTime)
    }

    fun isInTheSameDay(): Boolean {
        return is24HoursApart() && dateTime.toLocalDate().dayOfMonth == LocalDateTime.now().dayOfMonth
    }

    fun is24HoursApart(): Boolean {
        return LocalDateTime.now().isAfter(this.dateTime.minusHours(24))
    }

    fun is7DaysApart(): Boolean {
        return LocalDateTime.now().isAfter(this.dateTime.minusDays(7))
    }

    fun isOneYearApart(): Boolean {
        return LocalDateTime.now().year != dateTime.year
    }

    fun printExplicitDate() =
        when {
            this.isOutdated() -> "Outdated"
            this.isInTheSameDay() -> "Today"
            this.is24HoursApart() -> "Next Day"
            this.is7DaysApart() -> this.dateTime.dayOfWeek.name.normalizeWord()
            this.isOneYearApart() -> "${this.dateTime.dayOfMonth} " +
                    this.dateTime.month.name.substring(0..2).normalizeWord() +
                    " ${this.dateTime.year}"
            else -> "${this.dateTime.dayOfMonth} " +
                    this.dateTime.month.name.normalizeWord()
        }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Event

        if (name != other.name) return false
        if (dateTime != other.dateTime) return false
        if (duration != other.duration) return false
        if (type != other.type) return false
        if (bandId != other.bandId) return false

        return true
    }

    override fun hashCode(): Int {
        var result = name.hashCode()
        result = 31 * result + dateTime.hashCode()
        result = 31 * result + duration.hashCode()
        result = 31 * result + type.hashCode()
        result = 31 * result + bandId.hashCode()
        return result
    }

    override fun toString(): String {
        return "Event(id=$id, name='$name', dateTime=$dateTime, type=$type, bandId=$bandId)"
    }

    companion object {
        val EMPTY = Event("", LocalDateTime.now(), Duration.ZERO, BandItEnums.Event.Type.Simple, -1)
    }

}