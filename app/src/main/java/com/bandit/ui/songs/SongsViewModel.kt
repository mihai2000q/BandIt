package com.bandit.ui.songs

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.bandit.constant.Constants
import com.bandit.data.model.Album
import com.bandit.data.model.Song
import com.bandit.data.repository.AlbumRepository
import com.bandit.data.repository.SongRepository
import com.bandit.di.DILocator
import com.bandit.util.ParserUtils
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import java.time.Duration
import java.time.LocalDate

class SongsViewModel : ViewModel() {
    private val _songRepository = SongRepository(DILocator.getDatabase())
    private val _albumRepository = AlbumRepository(DILocator.getDatabase())
    private val _songs = MutableLiveData(_songRepository.list)
    private val _albums = MutableLiveData(_albumRepository.list)
    val songs: LiveData<List<Song>> = _songs
    val albums: LiveData<List<Album>> = _albums
    val selectedSong: MutableLiveData<Song> = MutableLiveData()
    val selectedAlbum: MutableLiveData<Album> = MutableLiveData()
    val albumMode = MutableLiveData(false)

    enum class SongFilter { Name, ReleaseDate, Duration}
    enum class AlbumFilter { Name, ReleaseDate, Label}
    val songFilters = MutableLiveData<MutableMap<SongFilter, String>>(mutableMapOf())
    val albumFilters = MutableLiveData<MutableMap<AlbumFilter, String>>(mutableMapOf())
    init {
        SongFilter.values().forEach { songFilters.value?.put(it, "") }
        AlbumFilter.values().forEach { albumFilters.value?.put(it, "") }
    }
    suspend fun addSong(song: Song) = coroutineScope {
        launch { _songRepository.add(song) }.join()
        this@SongsViewModel.refreshSongs()
    }
    suspend fun removeSong(song: Song) = coroutineScope {
        launch { _songRepository.remove(song) }.join()
        if(song.albumId != null)
            _albumRepository.removeSong(_albums.value!!
                .first { it.id == song.albumId }, song)
        this@SongsViewModel.refreshSongs()
    }
    suspend fun editSong(song: Song) = coroutineScope {
        launch { _songRepository.edit(song) }.join()
        if(song.albumId != null)
            this@SongsViewModel.editSongFromAlbum(_albums.value!!
                .first { it.id == song.albumId }, song)
        this@SongsViewModel.refreshSongs()
    }
    fun filterSongs(
        name: String?,
        releaseDate: LocalDate? = null,
        albumName: String? = null,
        duration: Duration? = null
    ) {
        _songs.value = _songRepository.filterSongs(name, releaseDate, albumName, duration)
    }

    suspend fun addAlbum(album: Album) = coroutineScope {
        launch { _albumRepository.add(album) }.join()
        this@SongsViewModel.refreshAlbums()
    }

    suspend fun removeAlbum(album: Album) = coroutineScope {
        launch {
            _albumRepository.remove(album)
            _songs.value!!
                .filter { it.albumId == album.id }
                .forEach {
                    it.albumId = null
                    it.albumName = null
                    this@SongsViewModel.editSong(it)
            }
        }.join()
        this@SongsViewModel.refreshAlbums()
    }

    suspend fun editAlbum(album: Album) = coroutineScope {
        launch { _albumRepository.edit(album)
            if(didAlbumChangeName(album))
                _songs.value!!
                    .filter { it.albumId == album.id }
                    .forEach {
                        it.albumName = album.name
                        this@SongsViewModel.editSong(it)
                    }
        }.join()
        this@SongsViewModel.refreshAlbums()
    }

    suspend fun addSongToAlbum(album: Album, song: Song) = coroutineScope {
        launch { this@SongsViewModel.editSong(_albumRepository.addSong(album, song)) }.join()
        this@SongsViewModel.refreshAlbums()
    }

    suspend fun removeSongFromAlbum(album: Album, song: Song) = coroutineScope {
        launch { this@SongsViewModel.editSong(_albumRepository.removeSong(album, song)) }.join()
        this@SongsViewModel.refreshAlbums()
    }

    fun getSongsWithoutAnAlbum() = _songRepository.getSongsWithoutAnAlbum()

    fun filterAlbums(
        name: String?,
        releaseDate: LocalDate? = null,
        label: String? = null,
        duration: Duration? = null
    ) {
        _albums.value = _albumRepository.filterAlbums(name, releaseDate, label, duration)
    }

    fun getSongFiltersOn() = songFilters.value?.filter { it.value.isNotBlank() }!!.size

    fun getAlbumFiltersOn() = albumFilters.value?.filter { it.value.isNotBlank() }!!.size

    private fun editSongFromAlbum(album: Album, song: Song) {
        _albumRepository.editSong(album, song)
    }

    private fun didAlbumChangeName(album: Album) = _albums.value!!.first{ it.id == album.id }.name == album.name

    private fun refreshSongs() {
        _songs.value = _songRepository.list
        with(songFilters.value!!) {
            if(this.any { it.value != "" })
                this@SongsViewModel.filterSongs(
                    name = this[SongFilter.Name],
                    releaseDate = if(this[SongFilter.ReleaseDate].isNullOrEmpty())
                        null
                    else
                        ParserUtils.parseDate(this[SongFilter.ReleaseDate]),
                    albumName = null,
                    duration = if(this[SongFilter.Duration].isNullOrEmpty())
                        null
                    else
                        ParserUtils.parseDurationText(this[SongFilter.Duration])
                )
        }
    }

    private fun refreshAlbums() {
        _albums.value = _albumRepository.list
        with(albumFilters.value!!) {
            if(this.any { it.value != "" })
                this@SongsViewModel.filterAlbums(
                    name = this[AlbumFilter.Name],
                    releaseDate = if(this[AlbumFilter.ReleaseDate].isNullOrEmpty())
                        null
                    else
                        ParserUtils.parseDate(this[AlbumFilter.ReleaseDate]),
                    label = this[AlbumFilter.Label]
                )
        }
    }

    companion object {
        const val TAG = Constants.Song.VIEW_MODEL_TAG
    }
}