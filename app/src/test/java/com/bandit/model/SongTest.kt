package com.bandit.model

import com.bandit.constant.Constants
import com.bandit.data.model.Song
import org.junit.Assert.*
import org.junit.Test
import java.time.Duration
import java.time.LocalDate

class SongTest {
    @Test
    fun song_init() {
        val song = Song(
            "new song",
            -1,
            LocalDate.of(2012,9,13),
            Duration.ofSeconds(90),
            "new album",
            null
        )
        assertEquals(song.name, "new song")
        assertEquals(song.releaseDate, LocalDate.parse("2012-09-13"))
        assertEquals(song.bandId, -1)
        assertEquals(song.albumName, "new album")
        assertNull(song.albumId)
        assertEquals(song.duration, Duration.ofSeconds(90))
        assertNotNull(song.id)
    }
    @Test
    fun song_init_id() {
        //Repeated tests improvised
        for (i in 1..100) {
            val song = Song.EMPTY
            assertNotNull(song.id)
            if (song.id < 0 || song.id > Constants.MAX_NR_ITEMS)
                fail("The Id should be between these boundaries")
        }
    }
    @Test
    fun song_equals() {
        val song = Song(
            "new song",
            -1,
            LocalDate.of(2012,9,13),
            Duration.ofSeconds(90),
            "new album",
            null
        )
        val song2 = Song(
            "new song 2",
            -1,
            LocalDate.of(2012,9,13),
            Duration.ofSeconds(90),
            "new album",
            null
        )
        val song3 = Song(
            "new song",
            -1,
            LocalDate.of(2012,9,13),
            Duration.ofSeconds(90),
            "new album",
            null
        )
        assertEquals(song, song3)
        assertNotEquals(song, song2)
    }
    @Test
    fun song_sort_by_release_date() {
        val outcome = mutableListOf(
            Song(
                "new song 3",
                -1,
                LocalDate.of(2011,9,13),
                Duration.ofSeconds(90),
                "new album",
                null
            ),
            Song(
                "new song 4",
                -1,
                LocalDate.now(),
                Duration.ofSeconds(90),
                "new album",
                null
            ),
            Song(
                "new song 1",
                -1,
                LocalDate.of(2010,9,13),
                Duration.ofSeconds(90),
                "new album",
                null
            ),
            Song(
                "new song 2",
                -1,
                LocalDate.of(2010,10,13),
                Duration.ofSeconds(90),
                "new album",
                null
            )
        )
        outcome.sort()
        val expected = listOf(
            Song(
                "new song 1",
                -1,
                LocalDate.of(2010,9,13),
                Duration.ofSeconds(90),
                "new album",
                null
            ),
            Song(
                "new song 2",
                -1,
                LocalDate.of(2010,10,13),
                Duration.ofSeconds(90),
                "new album",
                null
            ),
            Song(
                "new song 3",
                -1,
                LocalDate.of(2011,9,13),
                Duration.ofSeconds(90),
                "new album",
                null
            ),
            Song(
                "new song 4",
                -1,
                LocalDate.now(),
                Duration.ofSeconds(90),
                "new album",
                null
            )
        )
        assertEquals(outcome, expected)
    }
}