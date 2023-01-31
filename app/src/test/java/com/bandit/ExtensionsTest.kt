package com.bandit

import com.bandit.extension.get2Characters
import com.bandit.extension.print
import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.Duration

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
}