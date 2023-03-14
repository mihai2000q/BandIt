package com.bandit.ui.songs

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.viewModelScope
import com.bandit.R
import com.bandit.ui.component.AndroidComponents
import com.bandit.constant.Constants
import com.bandit.data.model.Album
import com.bandit.data.model.Song
import com.bandit.ui.band.BandViewModel
import com.bandit.ui.template.SongDialogFragment
import com.bandit.util.AndroidUtils
import com.bandit.util.ParserUtils

class SongAddDialogFragment(private val selectedAlbum: Album = Album.EMPTY) : SongDialogFragment() {
    private val bandViewModel: BandViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        with(binding) {
            songButton.setText(R.string.bt_add)
            songButton.setOnClickListener {
                if(validateFields())
                    AndroidUtils.loadDialogFragment(viewModel.viewModelScope,
                        this@SongAddDialogFragment) { this@SongAddDialogFragment.addSong() }
            }
        }
    }

    private suspend fun addSong() {
        with(binding) {
            AndroidUtils.hideKeyboard(
                super.requireActivity(),
                Context.INPUT_METHOD_SERVICE,
                songButton
            )
            val newSong = if(selectedAlbum.isEmpty())
                Song(
                    name = songEtName.text.toString(),
                    bandId = bandViewModel.band.value!!.id,
                    releaseDate =  ParserUtils.parseDate(songEtReleaseDate.text.toString()),
                    duration = ParserUtils.parseDurationText(songEtDuration.text.toString())
                )
            else
                Song(
                    name = songEtName.text.toString(),
                    bandId = bandViewModel.band.value!!.id,
                    releaseDate = ParserUtils.parseDate(songEtReleaseDate.text.toString()),
                    duration = ParserUtils.parseDurationText(songEtDuration.text.toString()),
                    albumName = selectedAlbum.name,
                    albumId = selectedAlbum.id
                )
            viewModel.addSong(newSong)
            AndroidComponents.toastNotification(
                super.requireContext(),
                resources.getString(R.string.song_add_toast)
            )
            super.dismiss()
            songEtName.setText("")
            songEtReleaseDate.setText("")
            songEtDuration.setText("")
        }
    }

    companion object {
        const val TAG = Constants.Song.ADD_TAG
    }
}