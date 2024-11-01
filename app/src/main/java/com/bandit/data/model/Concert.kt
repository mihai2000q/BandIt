package com.bandit.data.model

import com.bandit.constant.BandItEnums
import com.bandit.util.AndroidUtils
import java.time.Duration
import java.time.LocalDateTime

data class Concert(
    override val name: String,
    override val dateTime: LocalDateTime,
    override val duration: Duration,
    override val bandId: Long,
    val city: String?,
    val country: String?,
    val place: String?,
    val concertType: BandItEnums.Concert.Type,
    override val id: Long = AndroidUtils.generateRandomLong()
) : Event(name, dateTime, duration, BandItEnums.Event.Type.Concert, bandId, id) {
    companion object {
        val EMPTY = Concert("", LocalDateTime.now(), Duration.ZERO, -1,
            "", "", "", BandItEnums.Concert.Type.Simple)
    }

    override fun toString(): String {
        return "Concert(id=$id, name='$name', dateTime=$dateTime, city='$city', country='$country', place='$place', type=$concertType)"
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
        if (concertType != other.concertType) return false
        if (bandId != other.bandId) return false

        return true
    }

    override fun hashCode(): Int {
        var result = name.hashCode()
        result = 31 * result + dateTime.hashCode()
        result = 31 * result + city.hashCode()
        result = 31 * result + country.hashCode()
        result = 31 * result + place.hashCode()
        result = 31 * result + concertType.hashCode()
        result = 31 * result + bandId.hashCode()
        return result
    }

}

