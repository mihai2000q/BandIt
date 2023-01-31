package com.bandit.songs

import com.bandit.BaseRepositoryTest
import com.bandit.data.model.Song
import com.bandit.data.repository.SongRepository
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import java.time.LocalDate

class SongRepositoryTest : BaseRepositoryTest<Song>() {
    private lateinit var songRepository: SongRepository
    @Before
    fun setup() {
        songRepository = SongRepository()
    }
    private fun import_data() {
        runBlocking {
            songRepository.add(
                Song(
                    "The prisoners",
                    -1,
                    LocalDate.of(2012,12,13)
                )
            )
            songRepository.add(
                Song(
                    "The warriors",
                    -1,
                    LocalDate.of(2018,12,9)
                )
            )
            songRepository.add(
                Song(
                    "The survivors",
                    -1,
                    LocalDate.of(2013,12,13)
                )
            )
            songRepository.add(
                Song(
                    "The avengers",
                    -1,
                    LocalDate.of(2014,12,13)
                )
            )
            songRepository.add(
                Song(
                    "The heroes",
                    -1,
                    LocalDate.of(2015,12,13)
                )
            )
            songRepository.add(
                Song(
                    "The angels",
                    -1,
                    LocalDate.of(2012,12,13)
                )
            )
        }
    }
    @Test
    fun song_repository_add() {
        runBlocking {
            songRepository.add(
                Song(
                    "new song",
                    -1,
                    LocalDate.of(2023, 12, 9)
                )
            )
            assert_song(
                songRepository,
                "new song",
                LocalDate.parse("2023-12-09")
            )
        }
    }
    @Test
    fun song_repository_remove() {
        import_data()
        val concertToRemove = songRepository.list[0]
        val before = songRepository.list.filter { it == concertToRemove }
        assertEquals(1, before.size)
        assertEquals(6, songRepository.list.size)
        runBlocking { songRepository.remove(concertToRemove) }
        val after = songRepository.list.filter { it == concertToRemove }
        assertEquals(0, after.size)
        assertEquals(5, songRepository.list.size)
        assertThrows(IndexOutOfBoundsException::class.java) {
            songRepository.list[5]
        }
    }
    @Test
    fun song_repository_edit() {
        import_data()
        var concertToEdit = songRepository.list[0]
        val newSong = Song(
            "new song",
            -1,
            LocalDate.of(2023,9,16)
        )
        //before
        assert_song(
            songRepository,
            "The prisoners",
            LocalDate.parse("2012-12-13")
        )
        concertToEdit = Song(
            newSong.name,
            newSong.bandId,
            newSong.releaseDate,
            id = concertToEdit.id
        )
        runBlocking { songRepository.edit(concertToEdit) }
        assert_song(
            songRepository,
            "new song",
            LocalDate.parse("2023-09-16")
        )
    }
    @Test
    fun song_repository_different_ids() {
        different_ids(songRepository, Song.EMPTY)
    }
    @Test
    fun song_repository_filter_name() {
        import_data()
        val outcome = songRepository.filterSongs(name = "prison")
        val expected = listOf(songRepository.list[0])
        assertEquals(outcome.size, 1)
        assertEquals(outcome, expected)
    }
    @Test
    fun song_repository_filter_release_date() {
        import_data()
        val outcome = songRepository.filterSongs(releaseDate = LocalDate.of(2012,12,13))
        val expected = listOf(
            Song(
                "The prisoners",
                -1,
                LocalDate.of(2012,12,13)
            ),
            Song(
                "The angels",
                -1,
                LocalDate.of(2012,12,13)
            )
        )
        assertEquals(outcome.size, 2)
        assertEquals(outcome, expected)
    }
    private fun assert_song(
        repository: SongRepository,
        name: String,
        releaseDate: LocalDate,
    ) {
        //always edits or removes first element
        assertNotNull(repository.list[0])
        assertEquals(name, repository.list[0].name)
        assertEquals(releaseDate, repository.list[0].releaseDate)
    }
}