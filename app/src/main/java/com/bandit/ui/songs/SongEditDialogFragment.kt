package com.bandit.ui.songs

import android.os.Bundle
import android.view.View
import com.bandit.R
import com.bandit.constant.Constants
import com.bandit.data.model.Song
import com.bandit.util.AndroidUtils

class SongEditDialogFragment : SongDialogFragment() {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        with(binding) {
            songButton.setText(R.string.bt_save)
            viewModel.selectedSong.observe(viewLifecycleOwner) {
                songEtName.setText(it.name)
                songEtReleaseDate.setText(it.releaseDate.toString())
                songEtAlbumName.setText(it.albumName)
                songEtDuration.setText(it.duration.toString())
            }
            songButton.setOnClickListener {
                viewModel.editSong(
                    Song(
                        songEtName.text.toString(),
                        viewModel.selectedSong.value!!.bandId,
                        AndroidUtils.parseDate(songEtReleaseDate),
                        songEtAlbumName.text.toString(),
                        viewModel.selectedSong.value!!.albumId,
                        AndroidUtils.parseDuration(songEtDuration),
                        viewModel.selectedSong.value!!.id
                    )
                )
                super.dismiss()
            }
        }
    }

    companion object {
        const val TAG = Constants.Song.EDIT_TAG
    }
}