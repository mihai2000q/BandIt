package com.bandit.ui.songs

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bandit.constant.Constants
import com.bandit.data.model.Album
import com.bandit.data.model.Song
import com.bandit.data.repository.AlbumRepository
import com.bandit.data.repository.SongRepository
import com.bandit.di.DILocator
import kotlinx.coroutines.launch
import java.time.Duration
import java.time.LocalDate

class SongsViewModel : ViewModel() {
    private val _songRepository = SongRepository(DILocator.database)
    private val _albumRepository = AlbumRepository(DILocator.database)
    private val _songs = MutableLiveData(_songRepository.list)
    private val _albums = MutableLiveData(_albumRepository.list)
    val songs: LiveData<List<Song>> = _songs
    val selectedSong: MutableLiveData<Song> = MutableLiveData()
    val selectedAlbum: MutableLiveData<Album> = MutableLiveData()
    //val filterSong: MutableLiveData<Song> = MutableLiveData()
    private val _albumMode = MutableLiveData<Boolean>()
    val albumMode: LiveData<Boolean> = _albumMode

    fun addSong(
        name: String,
        releaseDate: LocalDate,
        albumName: String,
        duration: Duration
    ) {
        viewModelScope.launch {
            val bandId = DILocator.database.currentBand.id
            val result = _albumRepository.isThereAnAlbum(albumName)
            var album: Album? = null
            val albumId: Long
            if(result == null) {
                album = Album(albumName, bandId)
                albumId = album.id
            }
            else
                albumId = result
            val song = Song(
                name,
                bandId,
                releaseDate,
                if(albumName.isEmpty() || albumName.isBlank()) null else albumName,
                if(albumName.isEmpty() || albumName.isBlank()) null else albumId,
                duration
            )
            album?.songs?.add(song)
            if (album != null) {
                launch { _albumRepository.add(album) }.join()
                _albums.value = _albumRepository.list
            }
            launch { _songRepository.add(song) }.join()
            _songs.value = _songRepository.list
        }
    }
    fun removeSong(song: Song): Boolean {
        var result = false
        viewModelScope.launch {

            launch { result = _songRepository.remove(song) }.join()
            _songs.value = _songRepository.list
        }
        return result
    }
    fun editSong(song: Song) {
        viewModelScope.launch {
            launch { _songRepository.edit(song) }.join()
            _songs.value = _songRepository.list
        }
    }
    fun filterSongs(
        name: String?,
        releaseDate: LocalDate?,
        albumName: String?,
        duration: Duration?
    ) {
        _songs.value = _songRepository.filterSongs(name, releaseDate, albumName, duration)
    }

    fun filterAlbums(
        name: String?,
        releaseDate: LocalDate?,
        label: String?,
        duration: Duration?
    ) {
        _albums.value = _albumRepository.filterAlbums(name, releaseDate, label, duration)
    }

    companion object {
        const val TAG = Constants.Song.VIEW_MODEL_TAG
    }
}