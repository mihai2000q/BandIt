package com.bandit.songs

import org.junit.Test
import java.time.Duration

class SongTest {
    @Test
    fun something() {
        val text = "00:55"
        val duration2 = Duration.parse(
            "PT${text[0]}${text[1]}M" +
                    "${text[3]}${text[4]}S"
        )
        val duration = Duration.parse("PT300S")
        println("${duration.toMinutes()} ARE TEH UKCING SECONDS")
    }
}