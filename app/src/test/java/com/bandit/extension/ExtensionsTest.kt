package com.bandit.extension

import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.Duration
import java.time.LocalDateTime

class ExtensionsTest {
    @Test
    fun string_extensions_get_2_characters() {
        val string = "2"
        val outcome = string.get2Characters()
        val expected = "02"
        assertEquals(outcome, expected)

        val string2 = "02"
        val outcome2 = string2.get2Characters()
        assertEquals(outcome2, expected)
    }
    @Test
    fun print_extensions_duration_print() {
        val duration = Duration.ofSeconds(90)
        val outcome = duration.print()
        val expected = "01:30"
        assertEquals(outcome, expected)

        val duration2 = Duration.ofSeconds(120)
        val outcome2 = duration2.print()
        val expected2 = "02:00"
        assertEquals(outcome2, expected2)
    }
    @Test
    fun print_extensions_date_time_print() {
        val localDateTime = LocalDateTime.of(2023,12,14,21,30)
        val outcome = localDateTime.print()
        val expected = "14-12-2023 21:30"
        assertEquals(expected, outcome)
    }
    @Test
    fun print_extensions_date_time_print_name() {
        val localDateTime = LocalDateTime.of(2023,12,14,21,30)
        val outcome = localDateTime.printName()
        val expected = "14 December 2023 21:30"
        assertEquals(expected, outcome)
    }
}