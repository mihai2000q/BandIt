package com.bandit.data.model

import com.bandit.helper.Generator
import java.time.LocalDate
import java.time.LocalDateTime

data class Concert(val name: String,
                   val dateTime: LocalDateTime,
                   val city: String,
                   val country: String,
                   val place: String) : java.io.Serializable {
    private val _id: Int = Generator.generateRandomConcertId()
    val id get() = _id
    companion object {
        fun getEmpty(): Concert = Concert("", LocalDateTime.now(), "", "", "")
    }
}

