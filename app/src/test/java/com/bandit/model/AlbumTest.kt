package com.bandit.model

import com.bandit.data.model.Album
import com.bandit.data.model.Song
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Test
import java.time.Duration
import java.time.LocalDate

class AlbumTest {
    @Test
    fun album_init() {
        val album = Album(
            "Legacy of the Beast",
            -1,
            LocalDate.of(2002,12,19),
            "The Black",
            mutableListOf(
                Song(
                    "legacy",
                    -1,
                    LocalDate.of(2002,12,18)
                ),
                Song(
                    "legacy2",
                    -1,
                    LocalDate.of(2002,12,10)
                )
            )
        )
        assertEquals(album.name, "Legacy of the Beast")
        assertEquals(album.releaseDate, LocalDate.parse("2002-12-19"))
        assertEquals(album.label, "The Black")
        assertEquals(album.songs.size, 2)
    }

    @Test
    fun album_equals() {
        val album1 = Album(
            "legacy",
            -1,
            LocalDate.of(2000,10,10),
            "The Label"
        )
        val album2 = Album(
            "legacy",
            -1,
            LocalDate.of(2000,10,10),
            "The Labell"
        )
        val album3 = Album(
            "legacy",
            -1,
            LocalDate.of(2000,10,10),
            "The Label"
        )
        assertEquals(album1, album3)
        assertNotEquals(album1.id, album2.id)
        assertNotEquals(album1, album2)
    }

    @Test
    fun album_sort_by_release_date() {
        val outcome = mutableListOf(
            Album(
                "album1",
                -1,
                LocalDate.parse("2002-02-10")
            ),
            Album(
                "album4",
                -1,
                LocalDate.parse("2010-02-10")
            ),
            Album(
                "album2",
                -1,
                LocalDate.parse("2003-02-10")
            ),
            Album(
                "album3",
                -1,
                LocalDate.parse("2006-02-10")
            )
        )
        outcome.sort()
        val expected = mutableListOf(
            Album(
                "album1",
                -1,
                LocalDate.parse("2002-02-10")
            ),
            Album(
                "album2",
                -1,
                LocalDate.parse("2003-02-10")
            ),
            Album(
                "album3",
                -1,
                LocalDate.parse("2006-02-10")
            ),
            Album(
                "album4",
                -1,
                LocalDate.parse("2010-02-10")
            )
        )
        assertEquals(expected, outcome)
    }
    @Test
    fun album_duration() {
        val songs = listOf(
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
        val album = Album(
            name = "album",
            bandId = -1,
            releaseDate = LocalDate.parse("2002-02-10"),
            "",
            songs.toMutableList()
        )
        assertEquals(6, album.duration.toMinutes())
    }
}