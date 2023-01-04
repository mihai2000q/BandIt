package com.bandit.data.model

import com.bandit.helper.Generator
import java.time.LocalDate
import java.time.LocalDateTime

data class Concert(val name: String,
                   val dateTime: LocalDateTime) : java.io.Serializable {
    private val _id: Int = Generator.generateRandomConcertId()
    val id get() = _id
    constructor(name: String, date: LocalDate)
            : this(name, LocalDateTime.parse("${date}T00:00"))
}

