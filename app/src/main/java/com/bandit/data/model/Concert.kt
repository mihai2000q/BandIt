package com.bandit.data.model

import com.bandit.constant.BandItEnums
import com.bandit.util.AndroidUtils
import java.time.LocalDateTime
import kotlin.math.abs

class Concert(
    val name: String,
    val dateTime: LocalDateTime,
    val city: String,
    val country: String,
    val place: String,
    val type: BandItEnums.Concert.Type,
    id: Int = AndroidUtils.generateRandomId(),
    private val _userUid: String? = ""
) : BaseModel(id), Comparable<Concert> {
    val userUid get() = _userUid
    companion object {
        fun getEmpty(): Concert = Concert("", LocalDateTime.now(),
            "", "", "", BandItEnums.Concert.Type.Simple)
    }

    fun is7DaysApart(): Boolean {
        return  LocalDateTime.now().year == this.dateTime.year &&
                LocalDateTime.now().month == this.dateTime.month &&
                abs(this.dateTime.dayOfMonth - LocalDateTime.now().dayOfMonth) <= 7
    }

    fun isOneYearApart(): Boolean {
        return this.dateTime.year - LocalDateTime.now().year > 0
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

