package com.bandit.repository

import com.bandit.data.model.Album
import com.bandit.data.model.Song
import com.bandit.data.repository.AlbumRepository
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test
import java.time.LocalDate

class AlbumRepositoryTest : BaseRepositoryTest<Album>() {
    private lateinit var albumRepository: AlbumRepository
    @Before
    fun setup() {
        albumRepository = AlbumRepository()
    }
    override fun import_data() {
        runBlocking {
            albumRepository.add(
                Album(
                    "Legacy of the beast",
                    -1,
                    LocalDate.parse("2003-10-23")
                )
            )
            albumRepository.add(
                Album(
                    "Numb",
                    -1,
                    LocalDate.parse("2003-10-23"),
                    "The linkin"
                )
            )
            albumRepository.add(
                Album(
                    "In The End",
                    -1,
                    LocalDate.parse("2004-10-23")
                )
            )
            albumRepository.add(
                Album(
                    "Feed Me",
                    -1,
                    LocalDate.parse("2010-10-23")
                )
            )
            albumRepository.add(
                Album(
                    "Legacy of the underworld",
                    -1,
                    LocalDate.parse("2015-10-23")
                )
            )
            albumRepository.add(
                Album(
                    "Abnormal",
                    -1,
                    LocalDate.parse("2021-10-23")
                )
            )
        }
    }
    @Test
    fun album_repository_different_ids() {
        different_ids(albumRepository, Album.EMPTY)
    }
    @Test
    fun album_repository_add() {
        runBlocking {
            albumRepository.add(
                Album(
                    "new song",
                    -1,
                    LocalDate.of(2023, 12, 9)
                )
            )
            assert_album(
                albumRepository,
                "new song",
                LocalDate.parse("2023-12-09")
            )
        }
    }
    @Test
    fun album_repository_remove() {
        repository_remove(albumRepository)
    }
    @Test
    fun album_repository_edit() {
        import_data()
        var albumToEdit = albumRepository.list[0]
        val newAlbum = Album(
            "new album",
            -1,
            LocalDate.of(2023,9,16)
        )
        //before
        assert_album(
            albumRepository,
            "Legacy of the beast",
            LocalDate.parse("2003-10-23")
        )
        albumToEdit = Album(
            newAlbum.name,
            newAlbum.bandId,
            newAlbum.releaseDate,
            id = albumToEdit.id
        )
        runBlocking { albumRepository.edit(albumToEdit) }
        assert_album(
            albumRepository,
            "new album",
            LocalDate.parse("2023-09-16")
        )
    }
    @Test
    fun album_repository_filter_name() {
        import_data()
        val outcome = albumRepository.filterAlbums(name = "numb")
        val expected = listOf(albumRepository.list[1])
        assertEquals(outcome.size, 1)
        assertEquals(outcome, expected)
    }
    @Test
    fun album_repository_filter_release_date() {
        import_data()
        val outcome = albumRepository.filterAlbums(releaseDate = LocalDate.parse("2003-10-23"))
        val expected = listOf(albumRepository.list[1], albumRepository.list[0])
        assertEquals(outcome.size, 2)
        assertEquals(outcome.sorted(), expected.sorted())
    }
    @Test
    fun album_repository_add_and_remove_song() {
        val album = Album(
            "legacy of the beast",
            -1,
            LocalDate.parse("2003-10-23")
        )
        val newSong = Song(
            "the prisoners",
            -1,
            LocalDate.parse("2000-10-23")
        )
        runBlocking {
            albumRepository.add(album)
            albumRepository.addSong(album, newSong)
        }
        val songs = albumRepository.list[0].songs
        assertEquals(songs.size, 1)
        assert_song(songs[0],newSong.name, newSong.releaseDate, album.id, album.name)
        val songAfterRemove: Song
        runBlocking {
            songAfterRemove = albumRepository.removeSong(album, newSong)
        }
        val songsAfterRemove = albumRepository.list[0].songs
        assertEquals(songsAfterRemove.size, 0)
        assert_song(songAfterRemove, songAfterRemove.name, songAfterRemove.releaseDate)
    }
    private fun assert_album(
        repository: AlbumRepository,
        name: String,
        releaseDate: LocalDate,
    ) {
        assertNotNull(repository.list[0])
        assertEquals(name, repository.list[0].name)
        assertEquals(releaseDate, repository.list[0].releaseDate)
    }
    private fun assert_song(
        song: Song,
        name: String,
        releaseDate: LocalDate,
        albumId: Long? = null,
        albumName: String? = null
    ) {
        assertNotNull(song)
        assertEquals(name, song.name)
        assertEquals(releaseDate, song.releaseDate)
        assertEquals(albumId, song.albumId)
        assertEquals(albumName, song.albumName)
    }
}