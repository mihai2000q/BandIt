package com.bandit.data.model

import com.bandit.helper.Generator
import java.time.LocalDateTime

data class Concert(val name: String,
                   val dateTime: LocalDateTime,
                   val city: String,
                   val country: String,
                   val place: String,
                   val type: Type) : java.io.Serializable, Comparable<Concert> {
    enum class Type {Tournament, Simple, Festival}
    private val _id: Int = Generator.generateRandomConcertId()
    val id get() = _id
    companion object {
        fun getEmpty(): Concert = Concert("", LocalDateTime.now(),
            "", "", "", Type.Simple)
    }

    override fun compareTo(other: Concert): Int {
        return this.dateTime.compareTo(other.dateTime)
    }

    override fun toString(): String {
        return "Concert(id=$id, name='$name', dateTime=$dateTime, city='$city', country='$country', place='$place', type=$type)"
    }

}

