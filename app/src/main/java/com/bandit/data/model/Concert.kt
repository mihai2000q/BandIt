package com.bandit.data.model

import com.bandit.constant.BandItEnums
import com.bandit.util.AndroidUtils
import java.time.LocalDateTime

data class Concert(
    val name: String,
    val dateTime: LocalDateTime,
    val city: String,
    val country: String,
    val place: String,
    val type: BandItEnums.Concert.Type,
    override val id: Long = AndroidUtils.generateRandomLong(),
    val userUid: String? = ""
) : BaseModel(id), Comparable<Concert> {
    companion object {
        fun getEmpty(): Concert = Concert("", LocalDateTime.now(),
            "", "", "", BandItEnums.Concert.Type.Simple)
    }

    fun isOutdated(): Boolean {
        return LocalDateTime.now().isAfter(this.dateTime)
    }

    fun is24HoursApart(): Boolean {
        return LocalDateTime.now().isAfter(this.dateTime.minusHours(24))
    }

    fun is7DaysApart(): Boolean {
        return LocalDateTime.now().isAfter(this.dateTime.minusDays(7))
    }

    fun isOneYearApart(): Boolean {
        return LocalDateTime.now().isBefore(this.dateTime.minusYears(1))
    }

    override fun compareTo(other: Concert): Int {
        return this.dateTime.compareTo(other.dateTime)
    }

    override fun toString(): String {
        return "Concert(id=$id, name='$name', dateTime=$dateTime, city='$city', country='$country', place='$place', type=$type)"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Concert

        if (name != other.name) return false
        if (dateTime != other.dateTime) return false
        if (city != other.city) return false
        if (country != other.country) return false
        if (place != other.place) return false
        if (type != other.type) return false

        return true
    }

    override fun hashCode(): Int {
        var result = name.hashCode()
        result = 31 * result + dateTime.hashCode()
        result = 31 * result + city.hashCode()
        result = 31 * result + country.hashCode()
        result = 31 * result + place.hashCode()
        result = 31 * result + type.hashCode()
        return result
    }

}

