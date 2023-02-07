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
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import java.time.Duration
import java.time.LocalDate

class SongsViewModel : ViewModel() {
    private val _songRepository = SongRepository(DILocator.database)
    private val _albumRepository = AlbumRepository(DILocator.database)
    private val _songs = MutableLiveData(_songRepository.list)
    private val _albums = MutableLiveData(_albumRepository.list)
    val songs: LiveData<List<Song>> = _songs
    val albums: LiveData<List<Album>> = _albums
    val selectedSong: MutableLiveData<Song> = MutableLiveData()
    val selectedAlbum: MutableLiveData<Album> = MutableLiveData()
    //val filterSong: MutableLiveData<Song> = MutableLiveData()
    val albumMode = MutableLiveData(false)

    suspend fun addSong(song: Song) = coroutineScope {
        launch { _songRepository.add(song) }.join()
        _songs.value = _songRepository.list
    }
    suspend fun removeSong(song: Song) = coroutineScope {
        launch { _songRepository.remove(song) }.join()
        _songs.value = _songRepository.list
        if(song.albumId != null)
            removeSongFromAlbum(_albums.value!!.first{ it.id == song.albumId }, song)
    }
    suspend fun editSong(song: Song) = coroutineScope {
        launch { _songRepository.edit(song) }.join()
        _songs.value = _songRepository.list
        if(song.albumId != null)
            editSongFromAlbum(_albums.value!!.first{ it.id == song.albumId }, song)
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
        _albums.value = _albumRepository.list
    }

    suspend fun removeAlbum(album: Album) = coroutineScope {
        launch { _albumRepository.remove(album) }.join()
        _albums.value = _albumRepository.list
        _songs.value!!
            .filter { it.albumId == album.id }
            .forEach {
                it.albumId = null
                it.albumName = null
                editSong(it)
            }
    }

    suspend fun editAlbum(album: Album) = coroutineScope {
        launch { _albumRepository.edit(album) }.join()
        if(didAlbumChangeName(album))
            _songs.value!!.filter { it.albumId == album.id }
                .forEach {
                    it.albumName = album.name
                    editSong(it)
                }
        _albums.value = _albumRepository.list
    }

    suspend fun addSongToAlbum(album: Album, song: Song) = coroutineScope {
        launch { editSong(_albumRepository.addSong(album, song)) }.join()
        _albums.value = _albumRepository.list
    }

    suspend fun removeSongFromAlbum(album: Album, song: Song) = coroutineScope {
        launch { editSong(_albumRepository.removeSong(album, song)) }.join()
        _albums.value = _albumRepository.list
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

    private suspend fun editSongFromAlbum(album: Album, song: Song) = coroutineScope {
        launch { _albumRepository.editSong(album, song) }.join()
        _albums.value = _albumRepository.list
    }

    private fun didAlbumChangeName(album: Album) = _albums.value!!.first{ it.id == album.id }.name == album.name

    companion object {
        const val TAG = Constants.Song.VIEW_MODEL_TAG
    }
}