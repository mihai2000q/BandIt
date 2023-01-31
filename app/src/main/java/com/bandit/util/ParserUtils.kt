package com.bandit.util

import java.time.Duration
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

object ParserUtils {

    fun parseDateTime(
        textDate: String?,
        textTime: String?
    ): LocalDateTime {
        return LocalDateTime.parse(
            "${
                if (textDate.isNullOrEmpty())
                    LocalDate.now().toString()
                else textDate
            }T" + if (textTime.isNullOrEmpty())
                LocalTime.MIDNIGHT.minusMinutes(1).toString()
            else
                textTime
        )
    }
    fun parseDate(
        text: String?
    ): LocalDate = LocalDate.parse(
        if (text.isNullOrEmpty())
            LocalDate.now().toString()
        else text
    )

    fun parseDuration(
        text: String?
    ) : Duration =
        if(text.isNullOrEmpty())
            Duration.ZERO
        else Duration.parse(
            "PT${text[0]}${text[1]}M" +
                    "${text[3]}${text[4]}S"
        )
}