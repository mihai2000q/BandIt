package com.bandit.util

import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.Duration
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

class ParserUtilsTest {
    @Test
    fun parserUtils_parse_date_time() {
        val date = "2020-08-21"
        val time = "21:30"
        val outcome = ParserUtils.parseDateTime(date, time)
        val expected = LocalDateTime.parse("2020-08-21T21:30")
        assertEquals(outcome, expected)
    }
    @Test
    fun parserUtils_parse_date_time_default() {
        val date = ""
        val time = ""
        val outcome = ParserUtils.parseDateTime(date, time)
        val expected = LocalDateTime.parse(LocalDate.now().toString() + "T"
                + LocalTime.MIDNIGHT.minusMinutes(1).toString())
        assertEquals(outcome, expected)

        val date2 = null
        val time2 = null
        val outcome2 = ParserUtils.parseDateTime(date2, time2)
        assertEquals(expected, outcome2)
    }
    @Test
    fun parserUtils_parse_date() {
        val date = "2020-08-21"
        val outcome = ParserUtils.parseDate(date)
        val expected = LocalDate.parse("2020-08-21")
        assertEquals(outcome, expected)
    }
    @Test
    fun parserUtils_parse_date_default() {
        val date = ""
        val outcome = ParserUtils.parseDate(date)
        val expected = LocalDate.now()
        assertEquals(outcome, expected)

        val date2 = null
        val outcome2 = ParserUtils.parseDate(date2)
        assertEquals(outcome2, expected)
    }
    @Test
    fun parserUtils_parse_duration_text() {
        val duration = "00:30"
        val outcome = ParserUtils.parseDurationText(duration)
        val expected = Duration.ofSeconds(30)
        assertEquals(outcome, expected)

        val duration2 = "01:35"
        val outcome2 = ParserUtils.parseDurationText(duration2)
        val expected2 = Duration.ofSeconds(95)
        assertEquals(outcome2, expected2)
    }
    @Test
    fun parserUtils_parse_duration_seconds() {
        val duration = 90L
        val outcome = ParserUtils.parseDurationSeconds(duration)
        val expected = Duration.ofMinutes(1).plusSeconds(30)
        assertEquals(outcome, expected)
    }
    @Test
    fun parserUtils_parse_duration_text_default() {
        val duration = ""
        val outcome = ParserUtils.parseDurationText(duration)
        val expected = Duration.ZERO
        val expected2 = Duration.ofSeconds(0)
        assertEquals(outcome, expected)
        assertEquals(outcome, expected2)

        val duration2 = null
        val outcome2 = ParserUtils.parseDurationText(duration2)
        assertEquals(outcome2, expected)
    }

}